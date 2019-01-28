/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2012, Telestax Inc and individual contributors
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

package org.restcomm.protocols.ss7.isup.impl.message.parameter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.restcomm.protocols.ss7.isup.ParameterException;
import org.restcomm.protocols.ss7.isup.message.parameter.LocationNumber;

/**
 * Start time:17:02:12 2009-03-29<br>
 * Project: mobicents-isup-stack<br>
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class LocationNumberImpl extends AbstractNAINumber implements LocationNumber {
	private static final long serialVersionUID = 1L;

	protected int numberingPlanIndicator;
    protected int internalNetworkNumberIndicator;
    protected int addressRepresentationRestrictedIndicator;
    protected int screeningIndicator;

    public LocationNumberImpl(int natureOfAddresIndicator, String address, int numberingPlanIndicator,
            int internalNetworkNumberIndicator, int addressRepresentationREstrictedIndicator, int screeningIndicator) {
        super(natureOfAddresIndicator, address);
        this.numberingPlanIndicator = numberingPlanIndicator;
        this.internalNetworkNumberIndicator = internalNetworkNumberIndicator;
        this.addressRepresentationRestrictedIndicator = addressRepresentationREstrictedIndicator;
        this.screeningIndicator = screeningIndicator;
    }

    public LocationNumberImpl(byte[] representation) throws ParameterException {
        super(representation);

    }

    public LocationNumberImpl() {
        super();

    }

    public LocationNumberImpl(ByteArrayInputStream bis) throws ParameterException {
        super(bis);

    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.mobicents.isup.parameters.AbstractNumber#decodeBody(java.io. ByteArrayInputStream)
     */

    public int decodeBody(ByteArrayInputStream bis) throws IllegalArgumentException {
        int b = bis.read() & 0xff;

        this.internalNetworkNumberIndicator = (b & 0x80) >> 7;
        this.numberingPlanIndicator = (b & 0x70) >> 4;
        this.addressRepresentationRestrictedIndicator = (b & 0x0c) >> 2;
        this.screeningIndicator = (b & 0x03);
        return 1;
    }

    public int encodeHeader(ByteArrayOutputStream bos) {
        doAddressPresentationRestricted();

        return super.encodeHeader(bos);
    }

    /**
     * makes checks on APRI - see NOTE to APRI in Q.763, p 23
     */
    protected void doAddressPresentationRestricted() {

        if (this.addressRepresentationRestrictedIndicator != _APRI_NOT_AVAILABLE)
            return;
        // NOTE 1 If the parameter is included and the address presentation
        // restricted indicator indicates
        // address not available, octets 3 to n( this are digits.) are omitted,
        // the subfields in items a - odd/evem, b -nai , c - ni and d -npi, are
        // coded with
        // 0's, and the subfield f - filler, is coded with 11.
        this.oddFlag = 0;
        this.natureOfAddresIndicator = 0;
        this.numberingPlanIndicator = 0;
        this.internalNetworkNumberIndicator = 0;

        this.screeningIndicator = _SI_NETWORK_PROVIDED;
        this.setAddress("");
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.mobicents.isup.parameters.AbstractNumber#encodeBody(java.io. ByteArrayOutputStream)
     */

    public int encodeBody(ByteArrayOutputStream bos) {
        int c = this.numberingPlanIndicator << 4;
        c |= (this.internalNetworkNumberIndicator << 7);
        c |= (this.addressRepresentationRestrictedIndicator << 2);
        c |= (this.screeningIndicator);
        bos.write(c);
        return 1;

    }

    public int decodeDigits(ByteArrayInputStream bis) throws ParameterException {

        if (bis.available() != 0) {
            return super.decodeDigits(bis);
        } else {
            this.setAddress("");
            return 0;
        }
    }

    public int getNumberingPlanIndicator() {
        return numberingPlanIndicator;
    }

    public void setNumberingPlanIndicator(int numberingPlanIndicator) {
        this.numberingPlanIndicator = numberingPlanIndicator;
    }

    public int getInternalNetworkNumberIndicator() {
        return internalNetworkNumberIndicator;
    }

    public void setInternalNetworkNumberIndicator(int internalNetworkNumberIndicator) {
        this.internalNetworkNumberIndicator = internalNetworkNumberIndicator;
    }

    public int getAddressRepresentationRestrictedIndicator() {
        return addressRepresentationRestrictedIndicator;
    }

    public void setAddressRepresentationRestrictedIndicator(int addressRepresentationREstrictedIndicator) {
        this.addressRepresentationRestrictedIndicator = addressRepresentationREstrictedIndicator;
    }

    public int getScreeningIndicator() {
        return screeningIndicator;
    }

    public void setScreeningIndicator(int screeningIndicator) {
        this.screeningIndicator = screeningIndicator;
    }

    public int getCode() {

        return _PARAMETER_CODE;
    }

    public String toString() {
        return "LocationNumber [numberingPlanIndicator=" + numberingPlanIndicator + ", internalNetworkNumberIndicator="
                + internalNetworkNumberIndicator + ", addressRepresentationRestrictedIndicator="
                + addressRepresentationRestrictedIndicator + ", screeningIndicator=" + screeningIndicator
                + ", natureOfAddresIndicator=" + natureOfAddresIndicator + ", oddFlag=" + oddFlag + ", address=" + address
                + "]";
    }
}
