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

package org.restcomm.protocols.ss7.map.service.mobility.locationManagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.restcomm.protocols.ss7.commonapp.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.commonapp.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.commonapp.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.commonapp.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.commonapp.primitives.MAPExtensionContainerTest;
import org.junit.Test;

import com.mobius.software.telco.protocols.ss7.asn.ASNDecodeResult;
import com.mobius.software.telco.protocols.ss7.asn.ASNParser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 *
 * @author Lasith Waruna Perera
 * @author yulianoifa
 *
 */
public class UpdateGprsLocationResponseTest {

    public byte[] getData() {
        return new byte[] { 48, 51, 4, 4, -111, 34, 34, -8, 48, 39, -96, 32, 48, 10, 6, 3, 42, 3, 4, 11, 12, 13, 14, 15, 48, 5,
                6, 3, 42, 3, 6, 48, 11, 6, 3, 42, 3, 5, 21, 22, 23, 24, 25, 26, -95, 3, 31, 32, 33, 5, 0, -128, 0 };
    };

    public byte[] getGSNAddressData() {
        return new byte[] { 23, 5, 38, 48, 81, 5 };
    };

    @Test
    public void testDecode() throws Exception {
    	ASNParser parser=new ASNParser();
    	parser.replaceClass(UpdateGprsLocationResponseImpl.class);
    	
    	byte[] data = this.getData();
    	ASNDecodeResult result=parser.decode(Unpooled.wrappedBuffer(data));
        assertFalse(result.getHadErrors());
        assertTrue(result.getResult() instanceof UpdateGprsLocationResponseImpl);
        UpdateGprsLocationResponseImpl prim = (UpdateGprsLocationResponseImpl)result.getResult();
        
        assertTrue(prim.getHlrNumber().getAddress().equals("22228"));
        assertEquals(prim.getHlrNumber().getAddressNature(), AddressNature.international_number);
        assertEquals(prim.getHlrNumber().getNumberingPlan(), NumberingPlan.ISDN);
        assertTrue(MAPExtensionContainerTest.CheckTestExtensionContainer(prim.getExtensionContainer()));
        assertTrue(prim.getAddCapability());
        assertTrue(prim.getSgsnMmeSeparationSupported());

    }

    @Test
    public void testEncode() throws Exception {
    	ASNParser parser=new ASNParser();
    	parser.replaceClass(UpdateGprsLocationResponseImpl.class);
    	
    	ISDNAddressStringImpl hlrNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "22228");
        MAPExtensionContainer extensionContainer = MAPExtensionContainerTest.GetTestExtensionContainer();

        UpdateGprsLocationResponseImpl prim = new UpdateGprsLocationResponseImpl(hlrNumber, extensionContainer, true, true);
        byte[] data=getData();
        ByteBuf buffer=parser.encode(prim);
        byte[] encodedData = new byte[buffer.readableBytes()];
        buffer.readBytes(encodedData);
        assertTrue(Arrays.equals(data, encodedData));
    }
}
