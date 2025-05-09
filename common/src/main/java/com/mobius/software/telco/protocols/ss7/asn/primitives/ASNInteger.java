package com.mobius.software.telco.protocols.ss7.asn.primitives;

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

/**
*
* @author yulian oifa
*
*/

import io.netty.buffer.ByteBuf;

import java.util.concurrent.ConcurrentHashMap;

import com.mobius.software.telco.protocols.ss7.asn.ASNClass;
import com.mobius.software.telco.protocols.ss7.asn.ASNParser;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNDecode;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNEncode;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNLength;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNTag;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNValidate;
import com.mobius.software.telco.protocols.ss7.asn.exceptions.ASNParsingComponentException;
import com.mobius.software.telco.protocols.ss7.asn.exceptions.ASNParsingComponentExceptionReason;

@ASNTag(asnClass=ASNClass.UNIVERSAL,tag=2,constructed=false,lengthIndefinite=false)
public class ASNInteger {
	private static Long[] shifts= {0x00FFFFFFFFFFFFFFL, 0x00FFFFFFFFFFFFL, 0x00FFFFFFFFFFL, 0x00FFFFFFFFL, 0x00FFFFFFL, 0x00FFFFL, 0x00FFL, 0x00L};
	
	private Long value;
	private String name;
	private Long minValue;
	private Long maxValue;
	private Boolean isRoot;
	
	//required for parser
	public ASNInteger() {
		
	}
	
	public ASNInteger(String name,Long minValue,Long maxValue,Boolean isRoot) {
		this.name = name;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.isRoot = isRoot;
	}
	
	public ASNInteger(Long value,String name,Long minValue,Long maxValue,Boolean isRoot) {
		this.value=value;
		this.name = name;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.isRoot = isRoot;
	}
	
	public ASNInteger(Integer value,String name,Integer minValue,Integer maxValue,Boolean isRoot) {
		if(value!=null)
			this.value=value.longValue();
		
		this.name = name;
		
		if(minValue!=null)
			this.minValue = minValue.longValue();
		
		if(maxValue!=null)
			this.maxValue = maxValue.longValue();
		
		this.isRoot = isRoot;
	}
	
	public Long getValue() {
		return value;
	}
	
	public Integer getIntValue() {
		if(value==null)
			return null;
		
		return value.intValue();
	}

	@ASNLength
	public Integer getLength(ASNParser parser) {
		if(value==null)
			return 0;
		
		return getLength(value);
	}
	
	@ASNEncode
	public void encode(ASNParser parser,ByteBuf buffer) {
		if(value==null)
			return;
		
		int length=getLength(value);
		for(int i=0,size=8*(length-1);i<length;i++,size-=8)
			buffer.writeByte((byte)((value>>size)&0xFF));				
	}
	
	@ASNDecode
	public Boolean decode(ASNParser parser,Object parent,ByteBuf buffer,ConcurrentHashMap<Integer,Object> mappedData,Boolean skipErrors,Integer level) {
		if(buffer.readableBytes()==0)
			return false;
		
		byte current=buffer.readByte();
		if((current & 0x0FF)==0x0FF)
			value=0xFFFFFFFFFFFFFFFFL;
		else
			value=(long)current;
		
		while(buffer.readableBytes()>0)
			value=(value<<8) | (buffer.readByte()&0x0FF);
		
		return false;
	}
	
	@ASNValidate
	public void validateElement() throws ASNParsingComponentException {
		if((minValue!=null || maxValue!=null) && value==null) {
			if(isRoot==null || !isRoot)
				throw new ASNParsingComponentException(name + " is required",ASNParsingComponentExceptionReason.MistypedParameter);
			else
				throw new ASNParsingComponentException(name + " is required",ASNParsingComponentExceptionReason.MistypedRootParameter);
		} else if(minValue!=null && value!=null && value<minValue){
			if(isRoot==null || !isRoot)
				throw new ASNParsingComponentException(name + " should be at least " + minValue,ASNParsingComponentExceptionReason.MistypedParameter);
			else
				throw new ASNParsingComponentException(name + " should be at least " + minValue,ASNParsingComponentExceptionReason.MistypedRootParameter);
		} else if(maxValue!=null && value!=null && value>maxValue){
			if(isRoot==null || !isRoot)
				throw new ASNParsingComponentException(name + " should be at most " + maxValue,ASNParsingComponentExceptionReason.MistypedParameter);
			else
				throw new ASNParsingComponentException(name + " should be at most " + maxValue,ASNParsingComponentExceptionReason.MistypedRootParameter);
		} 
			
	}
	
	public static int getLength(Long value)
	{
		Long testValue=Long.valueOf(value);
		int result=0;
		while(testValue!=0)
		{
			long currShift=shifts[result];
			result+=1;
			long currValue=(testValue&0xFF);
			testValue=((testValue>>8) & 0x00FFFFFFFFFFFFFFL);			
			if(testValue==0 && currValue>127)
				result++;
			
			if(testValue==currShift)
				testValue=0L;
		}
		
		if(result==0)
			result=1;
		
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		ASNInteger other = (ASNInteger) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		
		return true;
	}
}