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

package org.restcomm.protocols.ss7.sccp;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.restcomm.protocols.ss7.sccp.message.MessageFactory;
import org.restcomm.protocols.ss7.sccp.message.SccpDataMessage;
import org.restcomm.protocols.ss7.sccp.message.SccpNoticeMessage;
import org.restcomm.protocols.ss7.sccp.parameter.ParameterFactory;
import org.restcomm.protocols.ss7.sccp.parameter.ProtocolClass;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

/**
 *
 * @author Oleg Kulikov
 * @author baranowb
 * @author yulianoifa
 */
public interface SccpProvider extends Serializable {

    /**
     * Gets the access to message factory.
     *
     * @return message factory.
     */
    MessageFactory getMessageFactory();

    /**
     * Gets the access to parameter factory.
     *
     * @return parameter factory
     */
    ParameterFactory getParameterFactory();

    /**
     * Registers listener for some SSN. This is an equivalent of N-STATE request with User status==UIS (user in service)
     *
     * @param listener
     */
    void registerSccpListener(int ssn, SccpListener listener);

    /**
     * Removes listener for some SSN. This is an equivalent of N-STATE request with User status==UOS (user out of service)
     *
     */
    void deregisterSccpListener(int ssn);

    void registerManagementEventListener(UUID key,SccpManagementEventListener listener);

    void deregisterManagementEventListener(UUID key);

    /**
     * Sends message.
     *
     * @param message Message to be sent
     * @throws IOException
     */
    void send(SccpDataMessage message) throws IOException;

    /**
     * Sends a unitdata service UDTS, XUDTS, LUDTS message (with error inside).
     *
     * @param message Message to be sent
     * @throws IOException
     */
    void send(SccpNoticeMessage message) throws IOException;

    /**
     * Return the maximum length (in bytes) of the sccp message data
     *
     * @param calledPartyAddress
     * @param callingPartyAddress
     * @param msgNetworkId
     * @return
     */
    int getMaxUserDataLength(SccpAddress calledPartyAddress, SccpAddress callingPartyAddress, int msgNetworkId);

    /**
     * Request of N-COORD when the originating user is requesting permission to go out-of-service
     *
     * @param ssn
     */
    void coordRequest(int ssn);

    SccpConnection newConnection(int localSsn, ProtocolClass protocolClass) throws MaxConnectionCountReached;

    ConcurrentHashMap<Integer, SccpConnection> getConnections();

    /**
     * @return SCCP stack
     */
    SccpStack getSccpStack();
}
