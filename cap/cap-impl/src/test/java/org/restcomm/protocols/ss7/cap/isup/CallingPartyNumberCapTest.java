/*
 * Mobius Software LTD
 * Copyright 2019, Mobius Software LTD and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.restcomm.protocols.ss7.cap.isup;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.restcomm.protocols.ss7.commonapp.isup.CallingPartyNumberIsupImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.CallingPartyNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.CallingPartyNumber;
import org.testng.annotations.Test;

import com.mobius.software.telco.protocols.ss7.asn.ASNDecodeResult;
import com.mobius.software.telco.protocols.ss7.asn.ASNParser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

/**
 *
 * @author sergey vetyutnev
 * @author yulianoifa
 *
 */
public class CallingPartyNumberCapTest {

    public byte[] getData() {
        return new byte[] { 4, 8, (byte) 132, 17, 20, (byte) 135, 9, 80, 64, (byte) 7 }; // 247
    }

    public byte[] getIntData() {
        return new byte[] { (byte) 132, 17, 20, (byte) 135, 9, 80, 64, (byte) 7 }; // 247
    }

    @Test(groups = { "functional.decode", "isup" })
    public void testDecode() throws Exception {
    	ASNParser parser=new ASNParser(true);
    	parser.replaceClass(CallingPartyNumberIsupImpl.class);
    	
    	byte[] rawData = this.getData();
        ASNDecodeResult result=parser.decode(Unpooled.wrappedBuffer(rawData));

        assertFalse(result.getHadErrors());
        assertTrue(result.getResult() instanceof CallingPartyNumberIsupImpl);
        
        CallingPartyNumberIsupImpl elem = (CallingPartyNumberIsupImpl)result.getResult();        
        CallingPartyNumber cpn = elem.getCallingPartyNumber();
        assertTrue(ByteBufUtil.equals(elem.getValue(),Unpooled.wrappedBuffer(this.getIntData())));
        assertTrue(cpn.isOddFlag());
        assertEquals(cpn.getNumberingPlanIndicator(), 1);
        assertEquals(cpn.getScreeningIndicator(), 1);
        assertEquals(cpn.getAddressRepresentationRestrictedIndicator(), 0);
        assertEquals(cpn.getNumberIncompleteIndicator(), 0);
        assertEquals(cpn.getNatureOfAddressIndicator(), 4);
        assertTrue(cpn.getAddress().equals("41789005047"));
    }

    @Test(groups = { "functional.encode", "isup" })
    public void testEncode() throws Exception {
    	ASNParser parser=new ASNParser(true);
    	parser.replaceClass(CallingPartyNumberIsupImpl.class);
    	
        CallingPartyNumberIsupImpl elem = new CallingPartyNumberIsupImpl(new CallingPartyNumberImpl(Unpooled.wrappedBuffer(this.getIntData())));
        byte[] rawData = this.getData();
        ByteBuf buffer=parser.encode(elem);
        byte[] encodedData = new byte[buffer.readableBytes()];
        buffer.readBytes(encodedData);
        assertTrue(Arrays.equals(rawData, encodedData));

        CallingPartyNumber cpn = new CallingPartyNumberImpl(4, "41789005047", 1, 0, 0, 1);
        elem = new CallingPartyNumberIsupImpl(cpn);
        buffer=parser.encode(elem);
        encodedData = new byte[buffer.readableBytes()];
        buffer.readBytes(encodedData);
        assertTrue(Arrays.equals(rawData, encodedData));

        // int natureOfAddresIndicator, String address, int numberingPlanIndicator, int numberIncompleteIndicator, int
        // addressRepresentationREstrictedIndicator,
        // int screeningIndicator
    }
}
