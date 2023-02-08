/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * Copyright 2019, Mobius Software LTD and individual contributors
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

package org.restcomm.protocols.ss7.isup.message.parameter;

import java.io.Serializable;

/**
 * Start time:10:27:43 2009-07-23<br>
 * Project: restcomm-isup-stack<br>
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author yulianoifa
 */
public interface NatureOfAddressIndicator extends Serializable{

    /**
     * nature of address indicator value. See Q.763 - 3.9b
     */
    int _SPARE = 0;
    /**
     * nature of address indicator value. See Q.763 - 3.9b
     */
    int _SUBSCRIBER = 1;
    /**
     * nature of address indicator value. See Q.763 - 3.9b
     */
    int _UNKNOWN = 2;
    /**
     * nature of address indicator value. See Q.763 - 3.9b
     */
    int _NATIONAL = 3;
    /**
     * nature of address indicator value. See Q.763 - 3.9b
     */
    int _INTERNATIONAL = 4;
    /**
     * nature of address indicator value. See Q.763 - 3.9b
     */
    int _NETWORK_SPECIFIC = 5;
}
