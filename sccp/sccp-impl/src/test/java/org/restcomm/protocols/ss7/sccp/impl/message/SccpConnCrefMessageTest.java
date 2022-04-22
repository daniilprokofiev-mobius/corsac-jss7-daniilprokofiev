/*
 * TeleStax, Open Source Cloud Communications  Copyright 2012.
 * and individual contributors
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

package org.restcomm.protocols.ss7.sccp.impl.message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.LongMessageRuleType;
import org.restcomm.protocols.ss7.sccp.SccpProtocolVersion;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ImportanceImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.LocalReferenceImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.RefusalCauseImpl;
import org.restcomm.protocols.ss7.sccp.parameter.RefusalCauseValue;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class SccpConnCrefMessageTest {

    private Logger logger;
    private SccpStackImpl stack = new SccpStackImpl("SccpConnCrefMessageTestStack");
    private MessageFactoryImpl messageFactory;

    @BeforeMethod
    public void setUp() {
        this.messageFactory = new MessageFactoryImpl(stack);
        this.logger = LogManager.getLogger(SccpStackImpl.class.getCanonicalName());
    }

    @AfterMethod
    public void tearDown() {
    }

    public ByteBuf getDataCrefNoOptParams() {
        return Unpooled.wrappedBuffer(new byte[] { 0x03, 0x00, 0x00, 0x02, 0x09, 0x00 });
    }

    public ByteBuf getDataCrefOneOptParam() {
        return Unpooled.wrappedBuffer(new byte[] { 0x03, 0x00, 0x00, 0x02, 0x09, 0x01, 0x03, 0x02, 0x42, 0x08, 0x00 });
    }

    public ByteBuf getDataCrefAllParams() {
        return Unpooled.wrappedBuffer(new byte[] {
                0x03, 0x00, 0x00, 0x02, 0x09, 0x01, 0x03, 0x02, 0x42, 0x08, 0x0F, 0x05, 0x01, 0x02, 0x03, 0x04, 0x05, 0x12, 0x01, 0x07, 0x00
        });
    }

    @Test(groups = { "SccpMessage", "functional.decode" })
    public void testDecode() throws Exception {
        // ---- no optional params
        ByteBuf buf = this.getDataCrefNoOptParams();
        int type = buf.readByte();
        SccpConnCrefMessageImpl testObjectDecoded = (SccpConnCrefMessageImpl) messageFactory.createMessage(type, 1, 2, 0, buf, SccpProtocolVersion.ITU, 0);

        assertNotNull(testObjectDecoded);
        assertEquals(testObjectDecoded.getDestinationLocalReferenceNumber().getValue(), 2);
        assertEquals(testObjectDecoded.getRefusalCause().getValue(), RefusalCauseValue.ACCESS_CONGESTION);

        // ---- one optional param
        buf = this.getDataCrefOneOptParam();
        type = buf.readByte();
        testObjectDecoded = (SccpConnCrefMessageImpl) messageFactory.createMessage(type, 1, 2, 0, buf, SccpProtocolVersion.ITU, 0);

        assertNotNull(testObjectDecoded);
        assertEquals(testObjectDecoded.getDestinationLocalReferenceNumber().getValue(), 2);
        assertEquals(testObjectDecoded.getRefusalCause().getValue(), RefusalCauseValue.ACCESS_CONGESTION);
        assertNotNull(testObjectDecoded.getCalledPartyAddress());
        assertEquals(testObjectDecoded.getCalledPartyAddress().getSignalingPointCode(), 0);
        assertEquals(testObjectDecoded.getCalledPartyAddress().getSubsystemNumber(), 8);
        assertNull(testObjectDecoded.getCalledPartyAddress().getGlobalTitle());


        // ---- all param
        buf = this.getDataCrefAllParams();
        type = buf.readByte();
        testObjectDecoded = (SccpConnCrefMessageImpl) messageFactory.createMessage(type, 1, 2, 0, buf, SccpProtocolVersion.ITU, 0);

        assertNotNull(testObjectDecoded);
        assertEquals(testObjectDecoded.getDestinationLocalReferenceNumber().getValue(), 2);
        assertEquals(testObjectDecoded.getRefusalCause().getValue(), RefusalCauseValue.ACCESS_CONGESTION);
        assertNotNull(testObjectDecoded.getCalledPartyAddress());
        assertEquals(testObjectDecoded.getCalledPartyAddress().getSignalingPointCode(), 0);
        assertEquals(testObjectDecoded.getCalledPartyAddress().getSubsystemNumber(), 8);
        assertNull(testObjectDecoded.getCalledPartyAddress().getGlobalTitle());
        MessageSegmentationTest.assertByteBufs(testObjectDecoded.getUserData(), Unpooled.wrappedBuffer(new byte[]{1, 2, 3, 4, 5}));
        assertEquals(testObjectDecoded.getImportance().getValue(), 7);
    }

    @Test(groups = { "SccpMessage", "functional.encode" })
    public void testEncode() throws Exception {
        // ---- no optional params
        SccpConnCrefMessageImpl original = new SccpConnCrefMessageImpl(0, 0);
        original.setDestinationLocalReferenceNumber(new LocalReferenceImpl(2));
        original.setRefusalCause(new RefusalCauseImpl(RefusalCauseValue.ACCESS_CONGESTION));

        EncodingResultData encoded = original.encode(stack, LongMessageRuleType.LONG_MESSAGE_FORBBIDEN, 272, logger, false, SccpProtocolVersion.ITU);

        assertEquals(encoded.getSolidData(), this.getDataCrefNoOptParams());

        // ---- one optional param
        original = new SccpConnCrefMessageImpl(0, 0);
        original.setDestinationLocalReferenceNumber(new LocalReferenceImpl(2));
        original.setRefusalCause(new RefusalCauseImpl(RefusalCauseValue.ACCESS_CONGESTION));
        original.setCalledPartyAddress(stack.getSccpProvider().getParameterFactory().createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null,0,  8));

        encoded = original.encode(stack, LongMessageRuleType.LONG_MESSAGE_FORBBIDEN, 272, logger, false, SccpProtocolVersion.ITU);

        assertEquals(encoded.getSolidData(), this.getDataCrefOneOptParam());

        // ---- all param
        original = new SccpConnCrefMessageImpl(0, 0);
        original.setDestinationLocalReferenceNumber(new LocalReferenceImpl(2));
        original.setRefusalCause(new RefusalCauseImpl(RefusalCauseValue.ACCESS_CONGESTION));
        original.setCalledPartyAddress(stack.getSccpProvider().getParameterFactory().createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null,0,  8));
        original.setUserData(Unpooled.wrappedBuffer(new byte[] {1, 2, 3, 4, 5}));
        original.setImportance(new ImportanceImpl((byte)15));

        encoded = original.encode(stack, LongMessageRuleType.LONG_MESSAGE_FORBBIDEN, 272, logger, false, SccpProtocolVersion.ITU);

        MessageSegmentationTest.assertByteBufs(encoded.getSolidData(), this.getDataCrefAllParams());
    }
}
