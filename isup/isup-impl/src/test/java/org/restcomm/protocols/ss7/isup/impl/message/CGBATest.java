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

package org.restcomm.protocols.ss7.isup.impl.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.restcomm.protocols.ss7.isup.message.CircuitGroupBlockingAckMessage;
import org.restcomm.protocols.ss7.isup.message.ISUPMessage;
import org.restcomm.protocols.ss7.isup.message.parameter.CallReference;
import org.restcomm.protocols.ss7.isup.message.parameter.RangeAndStatus;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

/**
 * Start time:09:26:46 2009-04-22<br>
 * Project: restcomm-isup-stack<br>
 * Test for CGBA
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author yulianoifa
 */
public class CGBATest extends MessageHarness {

    @Test
    public void testTwo_Params() throws Exception {
        ByteBuf message = getDefaultBody();

        CircuitGroupBlockingAckMessage cgb = super.messageFactory.createCGBA();
        ((AbstractISUPMessage) cgb).decode(message, messageFactory,parameterFactory);

        try {
            RangeAndStatus RS = (RangeAndStatus) cgb.getParameter(RangeAndStatus._PARAMETER_CODE);
            assertNotNull(RS);

            byte range = RS.getRange();
            assertEquals(range, 0x11);
            ByteBuf b = RS.getStatus();
            assertNotNull(b);
            
            assertEquals(b.readableBytes(), 3);
            assertTrue(ByteBufUtil.equals(b,Unpooled.wrappedBuffer(new byte[] { 0x02, 0x03, 0x04 })));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed on get parameter[" + CallReference._PARAMETER_CODE + "]:" + e);
        }

    }

    protected ByteBuf getDefaultBody() {
        byte[] message = {

        0x0C, (byte) 0x0B, CircuitGroupBlockingAckMessage.MESSAGE_CODE
                // Circuit group supervision message type
                , 0x01 // hardware failure oriented
                , 0x01 // ptr to variable part
                // no optional, so no pointer
                // RangeAndStatus._PARAMETER_CODE
                , 0x04, 0x11, 0x02, 0x03, 0x04 };
        return Unpooled.wrappedBuffer(message);
    }

    protected ISUPMessage getDefaultMessage() {
        return super.messageFactory.createCGBA();
    }
}
