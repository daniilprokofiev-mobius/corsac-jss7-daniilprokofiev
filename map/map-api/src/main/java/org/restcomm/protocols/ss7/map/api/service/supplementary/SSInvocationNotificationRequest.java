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

package org.restcomm.protocols.ss7.map.api.service.supplementary;

import java.util.List;

import org.restcomm.protocols.ss7.map.api.primitives.AddressStringImpl;
import org.restcomm.protocols.ss7.map.api.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainerImpl;

/**
 *
 MAP V3:
 *
 * ss-InvocationNotification OPERATION ::= { --Timer m ARGUMENT SS-InvocationNotificationArg RESULT SS-InvocationNotificationRes
 * -- optional ERRORS { dataMissing | unexpectedDataValue | unknownSubscriber} CODE local:72 }
 *
 * SS-InvocationNotificationArg ::= SEQUENCE { imsi [0] IMSI, msisdn [1] ISDN-AddressStringImpl, ss-Event [2] SS-Code, -- The
 * following SS-Code values are allowed : -- ect SS-Code ::= '00110001'B -- multiPTY SS-Code ::= '01010001'B -- cd SS-Code ::=
 * '00100100'B -- ccbs SS-Code ::= '01000100'B ss-EventSpecification [3] SS-EventSpecification OPTIONAL, extensionContainer [4]
 * ExtensionContainer OPTIONAL, ..., b-subscriberNumber [5] ISDN-AddressStringImpl OPTIONAL, ccbs-RequestState [6] CCBS-RequestState
 * OPTIONAL }
 *
 * SS-EventSpecification ::= SEQUENCE SIZE (1..2) OF AddressStringImpl
 *
 *
 * @author sergey vetyutnev
 *
 */
public interface SSInvocationNotificationRequest extends SupplementaryMessage {

    IMSIImpl getImsi();

    ISDNAddressStringImpl getMsisdn();

    SSCodeImpl getSsEvent();

    List<AddressStringImpl> getSsEventSpecification();

    MAPExtensionContainerImpl getExtensionContainer();

    ISDNAddressStringImpl getBSubscriberNumber();

    CCBSRequestState getCcbsRequestState();

}