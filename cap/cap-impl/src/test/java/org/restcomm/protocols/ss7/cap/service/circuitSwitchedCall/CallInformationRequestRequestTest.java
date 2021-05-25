/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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

package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.RequestedInformationType;
import org.restcomm.protocols.ss7.cap.primitives.CAPExtensionsTest;
import org.restcomm.protocols.ss7.inap.api.primitives.LegType;
import org.restcomm.protocols.ss7.inap.api.primitives.SendingLegIDImpl;
import org.testng.annotations.Test;

import com.mobius.software.telco.protocols.ss7.asn.ASNDecodeResult;
import com.mobius.software.telco.protocols.ss7.asn.ASNParser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class CallInformationRequestRequestTest {

    public byte[] getData1() {
        return new byte[] { 48, 16, (byte) 160, 9, 10, 1, 1, 10, 1, 2, 10, 1, 30, (byte) 163, 3, (byte) 128, 1, 2 };
    }

    public byte[] getData2() {
        return new byte[] { 48, 36, (byte) 160, 9, 10, 1, 1, 10, 1, 2, 10, 1, 30, (byte) 162, 18, 48, 5, 2, 1, 2, (byte) 129,
                0, 48, 9, 2, 1, 3, 10, 1, 1, (byte) 129, 1, (byte) 255, (byte) 163, 3, (byte) 128, 1, 2 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall" })
    public void testDecode() throws Exception {
    	ASNParser parser=new ASNParser(true);
    	parser.replaceClass(CallInformationRequestRequestImpl.class);
    	
    	byte[] rawData = this.getData1();
        ASNDecodeResult result=parser.decode(Unpooled.wrappedBuffer(rawData));

        assertFalse(result.getHadErrors());
        assertTrue(result.getResult() instanceof CallInformationRequestRequestImpl);
        
        CallInformationRequestRequestImpl elem = (CallInformationRequestRequestImpl)result.getResult();
        assertEquals(elem.getRequestedInformationTypeList().get(0), RequestedInformationType.callStopTime);
        assertEquals(elem.getRequestedInformationTypeList().get(1), RequestedInformationType.callConnectedElapsedTime);
        assertEquals(elem.getRequestedInformationTypeList().get(2), RequestedInformationType.releaseCause);
        assertEquals(elem.getLegID().getSendingSideID(), LegType.leg2);
        assertNull(elem.getExtensions());

        rawData = this.getData2();
        result=parser.decode(Unpooled.wrappedBuffer(rawData));

        assertFalse(result.getHadErrors());
        assertTrue(result.getResult() instanceof CallInformationRequestRequestImpl);
        
        elem = (CallInformationRequestRequestImpl)result.getResult();
        assertEquals(elem.getRequestedInformationTypeList().get(0), RequestedInformationType.callStopTime);
        assertEquals(elem.getRequestedInformationTypeList().get(1), RequestedInformationType.callConnectedElapsedTime);
        assertEquals(elem.getRequestedInformationTypeList().get(2), RequestedInformationType.releaseCause);
        assertEquals(elem.getLegID().getSendingSideID(), LegType.leg2);
        assertTrue(CAPExtensionsTest.checkTestCAPExtensions(elem.getExtensions()));
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall" })
    public void testEncode() throws Exception {
    	ASNParser parser=new ASNParser(true);
    	parser.replaceClass(CallInformationRequestRequestImpl.class);
    	
        ArrayList<RequestedInformationType> requestedInformationTypeList = new ArrayList<RequestedInformationType>();
        requestedInformationTypeList.add(RequestedInformationType.callStopTime);
        requestedInformationTypeList.add(RequestedInformationType.callConnectedElapsedTime);
        requestedInformationTypeList.add(RequestedInformationType.releaseCause);
        SendingLegIDImpl legID = new SendingLegIDImpl(LegType.leg2);

        CallInformationRequestRequestImpl elem = new CallInformationRequestRequestImpl(requestedInformationTypeList, null,
                legID);
        byte[] rawData = this.getData1();
        ByteBuf buffer=parser.encode(elem);
        byte[] encodedData = new byte[buffer.readableBytes()];
        buffer.readBytes(encodedData);
        assertTrue(Arrays.equals(rawData, encodedData));
        // ArrayList<RequestedInformationType> requestedInformationTypeList, CAPExtensions extensions, SendingSideID legID

        elem = new CallInformationRequestRequestImpl(requestedInformationTypeList, CAPExtensionsTest.createTestCAPExtensions(),
                legID);
        rawData = this.getData2();
        buffer=parser.encode(elem);
        encodedData = new byte[buffer.readableBytes()];
        buffer.readBytes(encodedData);
        assertTrue(Arrays.equals(rawData, encodedData));
    }
}