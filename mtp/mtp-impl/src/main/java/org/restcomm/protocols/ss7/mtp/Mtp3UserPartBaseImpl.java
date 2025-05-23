/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * Copyright 2019, Mobius Software LTD and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.restcomm.protocols.ss7.mtp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.concurrent.DefaultThreadFactory;

// lic dep 1

/**
 *
 * @author amit bhayani
 * @author sergey vetyutnev
 * @author yulianoifa
 *
 */
public abstract class Mtp3UserPartBaseImpl implements Mtp3UserPart {

	private static final Logger logger = LogManager.getLogger(Mtp3UserPartBaseImpl.class);

	public static final int _SI_SERVICE_ISUP = 5;
	public static final int _SI_SERVICE_SCCP = 3;

	protected static final String ROUTING_LABEL_FORMAT = "routingLabelFormat"; // we do not store this value
	protected static final String USE_LSB_FOR_LINKSET_SELECTION = "useLsbForLinksetSelection";

	private int slsFilter = 0x1F;

	// The count of threads that will be used for message delivering to
	// Mtp3UserPartListener's
	// For single thread model this value should be equal 1
	protected int deliveryTransferMessageThreadCount = Runtime.getRuntime().availableProcessors() * 2;
	// RoutingLabeFormat option
	private RoutingLabelFormat routingLabelFormat = RoutingLabelFormat.ITU;
	// If set to true, lowest bit of SLS is used for loadbalancing between Linkset
	// else highest bit of SLS is used.
	private boolean useLsbForLinksetSelection = false;

	protected boolean isStarted = false;

	private CopyOnWriteArrayList<Mtp3UserPartListener> userListeners = new CopyOnWriteArrayList<Mtp3UserPartListener>();
	// a thread pool for delivering Mtp3TransferMessage messages
	private ExecutorService msgDeliveryExecutors;
	private ScheduledExecutorService monitorExecutor;
	
	private AtomicLong localTaskIdentifier=new AtomicLong(1L);
	private ConcurrentHashMap<Long,DeliveryRunnableData> queuedTasks = new ConcurrentHashMap<Long,DeliveryRunnableData>();
	
	private Mtp3TransferPrimitiveFactory mtp3TransferPrimitiveFactory = null;

	public Mtp3UserPartBaseImpl(String productName) {
	}

	public int getDeliveryMessageThreadCount() {
		return this.deliveryTransferMessageThreadCount;
	}

	public void setDeliveryMessageThreadCount(int deliveryMessageThreadCount) throws Exception {
		if (deliveryMessageThreadCount > 0 && deliveryMessageThreadCount <= 100)
			this.deliveryTransferMessageThreadCount = deliveryMessageThreadCount;
	}

	@Override
	public void addMtp3UserPartListener(Mtp3UserPartListener listener) {
		this.userListeners.add(listener);
	}

	@Override
	public void removeMtp3UserPartListener(Mtp3UserPartListener listener) {
		this.userListeners.remove(listener);
	}

	@Override
	public RoutingLabelFormat getRoutingLabelFormat() {
		return this.routingLabelFormat;
	}

	@Override
	public void setRoutingLabelFormat(RoutingLabelFormat routingLabelFormat) throws Exception {
		if (routingLabelFormat != null)
			this.routingLabelFormat = routingLabelFormat;
	}

	@Override
	public boolean isUseLsbForLinksetSelection() {
		return useLsbForLinksetSelection;
	}

	@Override
	public void setUseLsbForLinksetSelection(boolean useLsbForLinksetSelection) throws Exception {
		this.useLsbForLinksetSelection = useLsbForLinksetSelection;
	}

	/*
	 * For classic MTP3 this value is maximum SIF length minus routing label length.
	 * This method should be overloaded if different message length is supported.
	 */
	@Override
	public int getMaxUserDataLength(int dpc) {
		switch (this.routingLabelFormat) {
		case ITU:
			// For PC_FORMAT_14, the MTP3 Routing Label takes 4 bytes - OPC/DPC
			// = 16 bits each and SLS = 4 bits
			return 272 - 4;
		case ANSI_Sls8Bit:
		case ANSI_Sls5Bit:
			// For PC_FORMAT_24, the MTP3 Routing Label takes 6 bytes - OPC/DPC
			// = 24 bits each and SLS = 8 bits
			return 272 - 7;
		default:
			// TODO : We don't support rest just yet
			return -1;

		}
	}

	@Override
	public Mtp3TransferPrimitiveFactory getMtp3TransferPrimitiveFactory() {
		return this.mtp3TransferPrimitiveFactory;
	}

	public void start() throws Exception {
		startNoLce();
	}

	protected void startNoLce() throws Exception {
		if (this.isStarted)
			return;

		if (!(this.routingLabelFormat == RoutingLabelFormat.ITU
				|| this.routingLabelFormat == RoutingLabelFormat.ANSI_Sls8Bit
				|| this.routingLabelFormat == RoutingLabelFormat.ANSI_Sls5Bit)) {
			throw new Exception("Invalid PointCodeFormat set. We support only ITU or ANSI now");
		}

		switch (this.routingLabelFormat) {
		case ITU:
			this.slsFilter = 0x0f;
			break;
		case ANSI_Sls5Bit:
			this.slsFilter = 0x1f;
			break;
		case ANSI_Sls8Bit:
			this.slsFilter = 0xff;
			break;
		default:
			throw new Exception("Invalid SLS length");
		}

		this.mtp3TransferPrimitiveFactory = new Mtp3TransferPrimitiveFactory(this.routingLabelFormat);
		this.msgDeliveryExecutors = Executors.newFixedThreadPool(this.deliveryTransferMessageThreadCount,
				new DefaultThreadFactory("Mtp3-DeliveryExecutor"));
		this.monitorExecutor = Executors.newScheduledThreadPool(1);
		this.monitorExecutor.scheduleAtFixedRate(new MonitorRunnable(), 60000L, 60, TimeUnit.SECONDS);
		this.isStarted = true;
	}

	public void stop() throws Exception {

		if (!this.isStarted)
			return;

		this.isStarted = false;
		this.msgDeliveryExecutors.shutdown();
	}

	/**
	 * Deliver an incoming message to the local user
	 *
	 * @param msg
	 * @param effectiveSls For the thread selection (for message delivering)
	 */
	protected void sendTransferMessageToLocalUser(Mtp3TransferPrimitive msg, int seqControl) {
		if (this.isStarted) {
			Long taskIdentifier = localTaskIdentifier.incrementAndGet();
			MsgTransferDeliveryHandler hdl = new MsgTransferDeliveryHandler(taskIdentifier, msg);
			seqControl = seqControl & slsFilter;
			
			// ok here we need to retain again
			msg.retain();
			
			Future<?> future = this.msgDeliveryExecutors.submit(hdl);
			
			queuedTasks.put(taskIdentifier, new DeliveryRunnableData(hdl, future));
		} else {
			logger.error(String.format(
					"Received Mtp3TransferPrimitive=%s but Mtp3UserPart is not started. Message will be dropped", msg));
		}
	}

	protected void sendPauseMessageToLocalUser(Mtp3PausePrimitive msg) {
		if (this.isStarted) {
			Long taskIdentifier = localTaskIdentifier.incrementAndGet();
			MsgSystemDeliveryHandler hdl = new MsgSystemDeliveryHandler(taskIdentifier,msg);
			Future<?> future = this.msgDeliveryExecutors.submit(hdl);
			queuedTasks.put(taskIdentifier, new DeliveryRunnableData(hdl, future));
		} else {
			logger.error(String
					.format("Received Mtp3PausePrimitive=%s but MTP3 is not started. Message will be dropped", msg));
		}
	}

	protected void sendResumeMessageToLocalUser(Mtp3ResumePrimitive msg) {
		if (this.isStarted) {
			Long taskIdentifier = localTaskIdentifier.incrementAndGet();
			MsgSystemDeliveryHandler hdl = new MsgSystemDeliveryHandler(taskIdentifier, msg);
			Future<?> future = this.msgDeliveryExecutors.submit(hdl);
			queuedTasks.put(taskIdentifier, new DeliveryRunnableData(hdl, future));
		} else {
			logger.error(String
					.format("Received Mtp3ResumePrimitive=%s but MTP3 is not started. Message will be dropped", msg));
		}
	}

	protected void sendStatusMessageToLocalUser(Mtp3StatusPrimitive msg) {
		if (this.isStarted) {
			Long taskIdentifier = localTaskIdentifier.incrementAndGet();
			MsgSystemDeliveryHandler hdl = new MsgSystemDeliveryHandler(taskIdentifier, msg);
			Future<?> future = this.msgDeliveryExecutors.submit(hdl);
			queuedTasks.put(taskIdentifier, new DeliveryRunnableData(hdl, future));
		} else {
			logger.error(String
					.format("Received Mtp3StatusPrimitive=%s but MTP3 is not started. Message will be dropped", msg));
		}
	}

	protected void sendEndCongestionMessageToLocalUser(Mtp3EndCongestionPrimitive msg) {
		if (this.isStarted) {
			Long taskIdentifier = localTaskIdentifier.incrementAndGet();
			MsgSystemDeliveryHandler hdl = new MsgSystemDeliveryHandler(taskIdentifier, msg);
			Future<?> future = this.msgDeliveryExecutors.submit(hdl);
			queuedTasks.put(taskIdentifier, new DeliveryRunnableData(hdl, future));
		} else {
			logger.error(String.format(
					"Received Mtp3EndCongestionPrimitive=%s but MTP3 is not started. Message will be dropped", msg));
		}
	}

	private class MsgTransferDeliveryHandler implements DeliveryRunnable {

		private Mtp3TransferPrimitive msg;
		private Long startTime;
		private Long taskIdentifier;
		
		public MsgTransferDeliveryHandler(Long taskIdentifier,Mtp3TransferPrimitive msg) {
			this.msg = msg;
			this.taskIdentifier = taskIdentifier;
		}

		public Long getStartTime() {
			return startTime;
		}

		@Override
		public void run() {
			try {
				this.startTime = System.currentTimeMillis();
				if (isStarted) {
					try {
						for (Mtp3UserPartListener lsn : userListeners) {
							lsn.onMtp3TransferMessage(this.msg);
						}
					} catch (Throwable e) {
						logger.error("Exception while delivering a system messages to the MTP3-user: " + e.getMessage(),
								e);
					}
				} else {
					logger.error(String.format(
							"Received Mtp3TransferPrimitive=%s but Mtp3UserPart is not started. Message will be dropped",
							msg));
				}
			} finally {
				// we have proceed the message should be good time to release the message here ,
				// lets release all
				try {
					msg.releaseFully();
				} catch (IllegalReferenceCountException ex) {
					// may be its already decreased
				}
			}
			
			queuedTasks.remove(taskIdentifier);
		}

		@Override
		public void logRunnable() {
			logger.info("TASK[" + this.getClass().getCanonicalName() + "],Message[" + msg.toString() + "],Data[" + msg.printBuffer() + "]");
		}
	}

	private class MsgSystemDeliveryHandler implements DeliveryRunnable {

		Mtp3Primitive msg;
		private Long startTime;
		private Long taskIdentifier;
		
		public MsgSystemDeliveryHandler(Long taskIdentifier,Mtp3Primitive msg) {
			this.msg = msg;
			this.taskIdentifier = taskIdentifier;
		}

		public Long getStartTime()
		{
			return this.startTime;
		}
		
		@Override
		public void run() {
			this.startTime = System.currentTimeMillis();
			
			if (isStarted) {
				try {
					for (Mtp3UserPartListener lsn : userListeners) {
						if (this.msg.getType() == Mtp3Primitive.PAUSE)
							lsn.onMtp3PauseMessage((Mtp3PausePrimitive) this.msg);
						if (this.msg.getType() == Mtp3Primitive.RESUME)
							lsn.onMtp3ResumeMessage((Mtp3ResumePrimitive) this.msg);
						if (this.msg.getType() == Mtp3Primitive.STATUS)
							lsn.onMtp3StatusMessage((Mtp3StatusPrimitive) this.msg);
						if (this.msg.getType() == Mtp3Primitive.END_CONGESTION)
							lsn.onMtp3EndCongestionMessage((Mtp3EndCongestionPrimitive) this.msg);
					}
				} catch (Throwable e) {
					logger.error("Exception while delivering a payload messages to the MTP3-user: " + e.getMessage(),
							e);
				}
			} else {
				logger.error(String.format(
						"Received Mtp3Primitive=%s but Mtp3UserPart is not started. Message will be dropped", msg));
			}
			
			queuedTasks.remove(taskIdentifier);
		}
		
		@Override
		public void logRunnable() {
			logger.info("TASK[" + this.getClass().getCanonicalName() + "] " + msg.toString());
		}
	}
	
	private class MonitorRunnable implements Runnable {

		@Override
		public void run() {
			if(isStarted) {
				List<Long> runnablesToRemove=new ArrayList<Long>();
				Iterator<Entry<Long, DeliveryRunnableData>> iterator = queuedTasks.entrySet().iterator();
				while(iterator.hasNext()) {
					Entry<Long, DeliveryRunnableData> currEntry = iterator.next();
					if(currEntry.getValue().getFuture().isDone())
						runnablesToRemove.add(currEntry.getKey());
					else if(currEntry.getValue().getRunnable().getStartTime()<System.currentTimeMillis() - 10*1000L) {
						//10 seconds is too high, however the number of stucked tasks should be minimal
						currEntry.getValue().getRunnable().logRunnable();
						runnablesToRemove.add(currEntry.getKey());
						//not sure it helps , however we at least get log of the killer message...
						currEntry.getValue().getFuture().cancel(true);			
					}
				}
				
				for(Long curr:runnablesToRemove) {
					queuedTasks.remove(curr);
				}
				
				logger.info("Remaing tasks in MTP executor " + queuedTasks.size());
			}
		}		
	}
}