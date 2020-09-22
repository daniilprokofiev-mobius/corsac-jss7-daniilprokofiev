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

package org.restcomm.protocols.ss7.map.api.service.pdpContextActivation;

import org.restcomm.protocols.ss7.map.api.primitives.GSNAddressImpl;
import org.restcomm.protocols.ss7.map.api.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainerImpl;

/**
 *
 MAP V3:
 *
 * noteMsPresentForGprs OPERATION ::= { --Timer m ARGUMENT NoteMsPresentForGprsArg RESULT NoteMsPresentForGprsRes -- optional
 * ERRORS { systemFailure | dataMissing | unexpectedDataValue | unknownSubscriber} CODE local:26 }
 *
 * NoteMsPresentForGprsArg ::= SEQUENCE { imsi [0] IMSI, sgsn-Address [1] GSN-Address, ggsn-Address [2] GSN-Address OPTIONAL,
 * extensionContainer [3] ExtensionContainer OPTIONAL, ...}
 *
 *
 * @author sergey vetyutnev
 *
 */
public interface NoteMsPresentForGprsRequest extends PdpContextActivationMessage {

    IMSIImpl getImsi();

    GSNAddressImpl getSgsnAddress();

    GSNAddressImpl getGgsnAddress();

    MAPExtensionContainerImpl getExtensionContainer();

}
