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

import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SupportedCamelPhases;

import com.mobius.software.telco.protocols.ss7.asn.primitives.ASNBitString;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
public class SupportedCamelPhasesImpl extends ASNBitString implements SupportedCamelPhases {
	private static final int _INDEX_Phase1 = 0;
    private static final int _INDEX_Phase2 = 1;
    private static final int _INDEX_Phase3 = 2;
    private static final int _INDEX_Phase4 = 3;

    public SupportedCamelPhasesImpl() {        
    }

    public SupportedCamelPhasesImpl(boolean phase1, boolean phase2, boolean phase3, boolean phase4) {
        this.setData(phase1, phase2, phase3, phase4);
    }

    protected void setData(boolean phase1, boolean phase2, boolean phase3, boolean phase4) {
        if (phase1)
            this.setBit(_INDEX_Phase1);
        if (phase2)
            this.setBit(_INDEX_Phase2);
        if (phase3)
            this.setBit(_INDEX_Phase3);
        if (phase4)
            this.setBit(_INDEX_Phase4);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberManagement. SupportedCamelPhases#getPhase1Supported()
     */
    public boolean getPhase1Supported() {
        return this.isBitSet(_INDEX_Phase1);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberManagement. SupportedCamelPhases#getPhase2Supported()
     */
    public boolean getPhase2Supported() {
        return this.isBitSet(_INDEX_Phase2);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberManagement. SupportedCamelPhases#getPhase3Supported()
     */
    public boolean getPhase3Supported() {
        return this.isBitSet(_INDEX_Phase3);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberManagement. SupportedCamelPhases#getPhase4Supported()
     */
    public boolean getPhase4Supported() {
        return this.isBitSet(_INDEX_Phase4);        
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SupportedCamelPhases [");

        if (getPhase1Supported())
            sb.append("Phase1Supported, ");
        if (getPhase2Supported())
            sb.append("Phase2Supported, ");
        if (getPhase3Supported())
            sb.append("Phase3Supported, ");
        if (getPhase4Supported())
            sb.append("Phase4Supported, ");

        sb.append("]");

        return sb.toString();
    }
}
