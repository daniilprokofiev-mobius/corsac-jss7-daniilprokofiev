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
import org.restcomm.protocols.ss7.isup.message.parameter.CallingPartyNumber;

/**
 * Start time:16:14:51 2009-03-29<br>
 * Project: mobicents-isup-stack<br>
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author Oleg Kulikoff
 */
public class CallingPartyNumberImpl extends AbstractNAINumber implements CallingPartyNumber {
	private static final long serialVersionUID = 1L;

	protected int numberingPlanIndicator;
    protected int numberIncompleteIndicator;
    protected int addressRepresentationRestrictedIndicator;
    protected int screeningIndicator;

    /**
     *
     * @param representation
     * @throws ParameterException
     */
    public CallingPartyNumberImpl(byte[] representation) throws ParameterException {
        super(representation);

    }

    public CallingPartyNumberImpl() {
        super();

    }

    /**
     *
     * @param bis
     * @throws ParameterException
     */
    public CallingPartyNumberImpl(ByteArrayInputStream bis) throws ParameterException {
        super(bis);

    }

    public CallingPartyNumberImpl(int natureOfAddresIndicator, String address, int numberingPlanIndicator,
            int numberIncompleteIndicator, int addressRepresentationREstrictedIndicator, int screeningIndicator) {
        super(natureOfAddresIndicator, address);
        this.numberingPlanIndicator = numberingPlanIndicator;
        this.numberIncompleteIndicator = numberIncompleteIndicator;
        this.addressRepresentationRestrictedIndicator = addressRepresentationREstrictedIndicator;
        this.screeningIndicator = screeningIndicator;
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.mobicents.isup.parameters.AbstractNumber#decodeBody(java.io. ByteArrayInputStream)
     */

    public int decodeBody(ByteArrayInputStream bis) throws IllegalArgumentException {
        int b = bis.read() & 0xff;

        this.numberIncompleteIndicator = (b & 0x80) >> 7;
        this.numberingPlanIndicator = (b & 0x70) >> 4;
        this.addressRepresentationRestrictedIndicator = (b & 0x0c) >> 2;
        this.screeningIndicator = (b & 0x03);

        return 1;
    }

    public int encodeHeader(ByteArrayOutputStream bos) {
        doAddressPresentationRestricted();
        return super.encodeHeader(bos);
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.mobicents.isup.parameters.AbstractNumber#encodeBody(java.io. ByteArrayOutputStream)
     */

    public int encodeBody(ByteArrayOutputStream bos) {

        int c = this.numberingPlanIndicator << 4;
        c |= (this.numberIncompleteIndicator << 7);
        c |= (this.addressRepresentationRestrictedIndicator << 2);
        c |= (this.screeningIndicator);
        bos.write(c);
        return 1;
    }

    /**
     * makes checks on APRI - see NOTE to APRI in Q.763, p 23
     */
    protected void doAddressPresentationRestricted() {

        if (this.addressRepresentationRestrictedIndicator == _APRI_NOT_AVAILABLE) {

            // NOTE 1 If the parameter is included and the address
            // presentation
            // restricted indicator indicates
            // address not available, octets 3 to n( this are digits.) are
            // omitted,
            // the subfields in items a - odd/evem, b -nai , c - ni and d -npi,
            // are
            // coded with
            // FIXME: add this filler
            // 0's, and the subfield f - filler, is coded with 11.
            this.oddFlag = 0;
            this.natureOfAddresIndicator = 0;
            this.numberIncompleteIndicator = 0;
            this.numberingPlanIndicator = 0;
            this.screeningIndicator = 3; // !!!
            this.setAddress("");
        }
    }

    public int decodeDigits(ByteArrayInputStream bis) throws ParameterException {

        if (this.addressRepresentationRestrictedIndicator == _APRI_NOT_AVAILABLE) {
            this.setAddress("");
            return 0;
        } else {
            return super.decodeDigits(bis);
        }
    }

    public int encodeDigits(ByteArrayOutputStream bos) {

        if (this.addressRepresentationRestrictedIndicator == _APRI_NOT_AVAILABLE) {
            return 0;
        } else {
            return super.encodeDigits(bos);
        }
    }

    public int getNumberingPlanIndicator() {
        return numberingPlanIndicator;
    }

    public void setNumberingPlanIndicator(int numberingPlanIndicator) {
        this.numberingPlanIndicator = numberingPlanIndicator;
    }

    public int getNumberIncompleteIndicator() {
        return numberIncompleteIndicator;
    }

    public void setNumberIncompleteIndicator(int numberIncompleteIndicator) {
        this.numberIncompleteIndicator = numberIncompleteIndicator;
    }

    public int getAddressRepresentationRestrictedIndicator() {
        return addressRepresentationRestrictedIndicator;
    }

    public void setAddressRepresentationREstrictedIndicator(int addressRepresentationREstrictedIndicator) {
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
        return "CallingPartyNumber [numberingPlanIndicator=" + numberingPlanIndicator + ", numberIncompleteIndicator="
                + numberIncompleteIndicator + ", addressRepresentationRestrictedIndicator="
                + addressRepresentationRestrictedIndicator + ", screeningIndicator=" + screeningIndicator
                + ", natureOfAddresIndicator=" + natureOfAddresIndicator + ", oddFlag=" + oddFlag + ", address=" + address
                + "]";
    }
}
