/*
 * TeleStax, Open Source Cloud Communications
 * Mobius Software LTD
 * Copyright 2012, Telestax Inc and individual contributors
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

package org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive;

import org.restcomm.protocols.ss7.cap.api.EsiBcsm.CallAcceptedSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.DpSpecificInfoAlt;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.OAbandonSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.OAnswerSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.OCalledPartyBusySpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.OChangeOfPositionSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.ODisconnectSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.OMidCallSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.ONoAnswerSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.OTermSeizedSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.RouteSelectFailureSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.TAnswerSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.TBusySpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.TChangeOfPositionSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.TDisconnectSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.TMidCallSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.TNoAnswerSpecificInfo;

import com.mobius.software.telco.protocols.ss7.asn.ASNClass;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNTag;

/**
 *
<code>
EventSpecificInformationBCSM {PARAMETERS-BOUND : bound} ::= CHOICE {
  routeSelectFailureSpecificInfo   [2] SEQUENCE {
    failureCause        [0] Cause {bound} OPTIONAL,
    ...
  },
  oCalledPartyBusySpecificInfo     [3] SEQUENCE {
    busyCause           [0] Cause {bound} OPTIONAL,
    ...
  },
  oNoAnswerSpecificInfo            [4] SEQUENCE {
    -- no specific info defined
    ...
  },
  oAnswerSpecificInfo              [5] SEQUENCE {
    destinationAddress     [50] CalledPartyNumber {bound} OPTIONAL,
    or-Call                [51] NULL OPTIONAL,
    forwardedCall          [52] NULL OPTIONAL,
    chargeIndicator        [53] ChargeIndicator OPTIONAL,
    ext-basicServiceCode   [54] Ext-BasicServiceCode OPTIONAL,
    ext-basicServiceCode2  [55] Ext-BasicServiceCode OPTIONAL,
    ...
  },
  oMidCallSpecificInfo             [6] SEQUENCE {
    midCallEvents          [1] CHOICE {
      dTMFDigitsCompleted    [3] Digits {bound},
      dTMFDigitsTimeOut      [4] Digits {bound}
    } OPTIONAL,
    ...
  },
  oDisconnectSpecificInfo          [7] SEQUENCE {
    releaseCause           [0] Cause {bound} OPTIONAL,
    ...
  },
  tBusySpecificInfo                [8] SEQUENCE {
    busyCause              [0] Cause {bound} OPTIONAL,
    callForwarded          [50] NULL OPTIONAL,
    routeNotPermitted      [51] NULL OPTIONAL,
    forwardingDestinationNumber [52] CalledPartyNumber {bound} OPTIONAL,
    ...
  },
  tNoAnswerSpecificInfo            [9] SEQUENCE {
    callForwarded               [50] NULL OPTIONAL,
    forwardingDestinationNumber [52] CalledPartyNumber {bound} OPTIONAL,
    ...
  },
  tAnswerSpecificInfo              [10] SEQUENCE {
    destinationAddress     [50] CalledPartyNumber {bound} OPTIONAL,
    or-Call                [51] NULL OPTIONAL,
    forwardedCall          [52] NULL OPTIONAL,
    chargeIndicator        [53] ChargeIndicator OPTIONAL,
    ext-basicServiceCode   [54] Ext-BasicServiceCode OPTIONAL,
    ext-basicServiceCode2  [55] Ext-BasicServiceCode OPTIONAL,
    ...
  },
  tMidCallSpecificInfo             [11] SEQUENCE {
    midCallEvents          [1] CHOICE {
      dTMFDigitsCompleted      [3] Digits {bound},
      dTMFDigitsTimeOut        [4] Digits {bound}
    } OPTIONAL,
    ...
  },
  tDisconnectSpecificInfo          [12] SEQUENCE {
    releaseCause [0] Cause {bound} OPTIONAL,
    ...
  },
  oTermSeizedSpecificInfo          [13] SEQUENCE {
    locationInformation    [50] LocationInformation OPTIONAL,
    ...
  },
  callAcceptedSpecificInfo         [20] SEQUENCE {
    locationInformation    [50] LocationInformation OPTIONAL,
    ...
  },
  oAbandonSpecificInfo             [21] SEQUENCE {
    routeNotPermitted      [50] NULL OPTIONAL,
    ...
  },
  oChangeOfPositionSpecificInfo    [50] SEQUENCE {
    locationInformation    [50] LocationInformation OPTIONAL,
    ...,
    metDPCriteriaList      [51] MetDPCriteriaList {bound} OPTIONAL
  },
  tChangeOfPositionSpecificInfo    [51] SEQUENCE {
    locationInformation    [50] LocationInformation OPTIONAL,
    ...,
    metDPCriteriaList      [51] MetDPCriteriaList {bound} OPTIONAL
  },
  dpSpecificInfoAlt                [52] DpSpecificInfoAlt {bound}
}
  -- Indicates the call related information specific to the event.
</code>
 *
 *
 * @author sergey vetyutnev
 * @author yulianoifa
 *
 */
@ASNTag(asnClass = ASNClass.UNIVERSAL,tag = 16,constructed = true,lengthIndefinite = false)
public interface EventSpecificInformationBCSM {

    RouteSelectFailureSpecificInfo getRouteSelectFailureSpecificInfo();

    OCalledPartyBusySpecificInfo getOCalledPartyBusySpecificInfo();

    ONoAnswerSpecificInfo getONoAnswerSpecificInfo();

    OAnswerSpecificInfo getOAnswerSpecificInfo();

    OMidCallSpecificInfo getOMidCallSpecificInfo();

    ODisconnectSpecificInfo getODisconnectSpecificInfo();

    TBusySpecificInfo getTBusySpecificInfo();

    TNoAnswerSpecificInfo getTNoAnswerSpecificInfo();

    TAnswerSpecificInfo getTAnswerSpecificInfo();

    TMidCallSpecificInfo getTMidCallSpecificInfo();

    TDisconnectSpecificInfo getTDisconnectSpecificInfo();

    OTermSeizedSpecificInfo getOTermSeizedSpecificInfo();

    CallAcceptedSpecificInfo getCallAcceptedSpecificInfo();

    OAbandonSpecificInfo getOAbandonSpecificInfo();

    OChangeOfPositionSpecificInfo getOChangeOfPositionSpecificInfo();

    TChangeOfPositionSpecificInfo getTChangeOfPositionSpecificInfo();

    DpSpecificInfoAlt getDpSpecificInfoAlt();

}