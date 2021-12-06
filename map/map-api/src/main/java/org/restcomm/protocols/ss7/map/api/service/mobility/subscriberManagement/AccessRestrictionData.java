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

package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement;

import com.mobius.software.telco.protocols.ss7.asn.ASNClass;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNTag;

/**
 *
<code>
 AccessRestrictionData ::= BIT STRING {
   utranNotAllowed (0),
   geranNotAllowed (1),
   ganNotAllowed (2),
   i-hspa-evolutionNotAllowed (3),
   e-utranNotAllowed (4),
   ho-toNon3GPP-AccessNotAllowed (5)
} (SIZE (2..8))
-- exception handling:
-- access restriction data related to an access type not supported by a node
-- shall be ignored
-- bits 6 to 7 shall be ignored if received and not understood
</code>
 *
 *
 *
 * @author sergey vetyutnev
 *
 */
@ASNTag(asnClass=ASNClass.UNIVERSAL,tag=3,constructed=false,lengthIndefinite=false)
public interface AccessRestrictionData {

    boolean getUtranNotAllowed();

    boolean getGeranNotAllowed();

    boolean getGanNotAllowed();

    boolean getIHspaEvolutionNotAllowed();

    boolean getEUtranNotAllowed();

    boolean getHoToNon3GPPAccessNotAllowed();

}
