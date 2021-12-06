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

package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.APN;

import com.mobius.software.telco.protocols.ss7.asn.primitives.ASNOctetString;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class APNImpl extends ASNOctetString implements APN {
	private static Charset ascii = Charset.forName("US-ASCII");

    public APNImpl() {
    }

    public APNImpl(byte[] data) {
    	setValue(Unpooled.wrappedBuffer(data));
    }

    public APNImpl(String apn) throws MAPException {
        if (apn == null)
            throw new MAPException("apn paramater must not be null");
        if (apn.length() == 0)
            throw new MAPException("apn paramater must not have zero length");

        setApnString(apn);
    }

    private void setApnString(String apn) throws MAPException {
        String[] ss = apn.split("\\.");
        int tLen = ss.length;
        for (String s : ss) {
            tLen += s.length();
        }
        byte[] data = new byte[tLen];
        if (data.length > 63)
            throw new MAPException("apn paramater encoded length is greater than max value (63): " + data.length);

        int i1 = 0;
        for (String s : ss) {
            data[i1++] = (byte) s.length();
            byte[] bb = s.getBytes(ascii);
            System.arraycopy(bb, 0, data, i1, bb.length);
            i1 += bb.length;
        }
        
        setValue(Unpooled.wrappedBuffer(data));
    }

    public byte[] getData() {
    	ByteBuf value=getValue();
    	if(value==null)
    		return null;
    	
    	byte[] data=new byte[value.readableBytes()];
    	value.readBytes(data);
        return data;
    }

    public String getApn() throws MAPException {
    	byte[] data=getData();
        if (data == null)
            throw new MAPException("Can not decode: data array is null");
        if (data.length < 2 || data.length > 63)
            throw new MAPException("Can not decode: data array must have length 2-63, found: " + data.length);

        List<String> ress = new ArrayList<String>();

        int i1 = 0;
        while (true) {
            int len = (data[i1++] & 0xFF);
            if (len > data.length - i1)
                throw new MAPException("Can not decode: read length byte has a value more then left byte count: " + len);

            byte[] bb = new byte[len];
            System.arraycopy(data, i1, bb, 0, len);
            String s = new String(bb, ascii);
            ress.add(s);

            i1 += len;
            if (i1 == data.length)
                break;
        }

        StringBuilder sb = new StringBuilder();
        i1 = 0;
        for (String s : ress) {
            if (i1 == 0)
                i1 = 1;
            else
                sb.append(".");
            sb.append(s);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        try {
            String s = this.getApn();

            StringBuilder sb = new StringBuilder();
            sb.append("APN [apn=");
            sb.append(s);
            sb.append("]");
            return sb.toString();
        } catch (MAPException e) {
            return super.toString();
        }
    }
}
