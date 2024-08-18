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

package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.restcomm.protocols.ss7.commonapp.api.circuitSwitchedCall.ChangeOfLocation;
import org.restcomm.protocols.ss7.commonapp.circuitSwitchedCall.ChangeOfLocationImpl;
import org.restcomm.protocols.ss7.commonapp.circuitSwitchedCall.DpSpecificCriteriaAltImpl;
import org.junit.Test;

import com.mobius.software.telco.protocols.ss7.asn.ASNDecodeResult;
import com.mobius.software.telco.protocols.ss7.asn.ASNParser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
*
* @author sergey vetyutnev
* @author yulianoifa
*
*/
public class DpSpecificCriteriaAltTest {

    public byte[] getData1() {
        return new byte[] { 48, 7, (byte) 160, 2, (byte) 132, 0, (byte) 129, 1, 15 };
    }

    @Test
    public void testDecode() throws Exception {
    	ASNParser parser=new ASNParser(true);
    	parser.replaceClass(DpSpecificCriteriaAltImpl.class);
    	
    	byte[] rawData = this.getData1();
        ASNDecodeResult result=parser.decode(Unpooled.wrappedBuffer(rawData));

        assertFalse(result.getHadErrors());
        assertTrue(result.getResult() instanceof DpSpecificCriteriaAltImpl);
        
        DpSpecificCriteriaAltImpl elem = (DpSpecificCriteriaAltImpl)result.getResult();        
        assertEquals(elem.getChangeOfPositionControlInfo().size(), 1);
        assertTrue(elem.getChangeOfPositionControlInfo().get(0).isInterPLMNHandOver());
        assertEquals((int)elem.getNumberOfDigits(), 15);
    }

    @Test
    public void testEncode() throws Exception {
    	ASNParser parser=new ASNParser(true);
    	parser.replaceClass(DpSpecificCriteriaAltImpl.class);
    	
        ArrayList<ChangeOfLocation> changeOfPositionControlInfo = new ArrayList<ChangeOfLocation>();
        ChangeOfLocationImpl changeOfLocation = new ChangeOfLocationImpl(ChangeOfLocationImpl.Boolean_Option.interPLMNHandOver);
        changeOfPositionControlInfo.add(changeOfLocation);
        DpSpecificCriteriaAltImpl elem = new DpSpecificCriteriaAltImpl(changeOfPositionControlInfo, 15);
        // ArrayList<ChangeOfLocation> changeOfPositionControlInfo, Integer numberOfDigits
        byte[] rawData = this.getData1();
        ByteBuf buffer=parser.encode(elem);
        byte[] encodedData = new byte[buffer.readableBytes()];
        buffer.readBytes(encodedData);
        assertTrue(Arrays.equals(rawData, encodedData));
    }
}
