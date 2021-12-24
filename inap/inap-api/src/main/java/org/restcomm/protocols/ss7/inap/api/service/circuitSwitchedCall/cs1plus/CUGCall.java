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

package org.restcomm.protocols.ss7.inap.api.service.circuitSwitchedCall.cs1plus;

/**
 *
<code>
CUGCall ::= ENUMERATED {
	nonCUGCall(0),
	spare(1),
	CUGCallWithOutgoingAccess(2),
	CUGCallWithoutOutgoingAccess(3)
}
</code>
 *
 *
 * @author yulian.oifa
 *
 */
public enum CUGCall {
	nonCUGCall(0),
	spare(1),
	cugCallWithOutgoingAccess(2),
	cugCallWithoutOutgoingAccess(3);

    private int code;

    private CUGCall(int code) {
        this.code = code;
    }

    public static CUGCall getInstance(int code) {
        switch (code) {
            case 0:
                return CUGCall.nonCUGCall;
            case 1:
                return CUGCall.spare; 
            case 2:
                return CUGCall.cugCallWithOutgoingAccess;
            case 3:
                return CUGCall.cugCallWithoutOutgoingAccess; 
        }

        return null;
    }

    public int getCode() {
        return code;
    }
}
