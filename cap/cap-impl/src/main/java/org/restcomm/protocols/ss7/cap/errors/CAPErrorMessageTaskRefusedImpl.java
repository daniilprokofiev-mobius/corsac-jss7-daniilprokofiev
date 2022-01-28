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

package org.restcomm.protocols.ss7.cap.errors;

import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorCode;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessageTaskRefused;
import org.restcomm.protocols.ss7.cap.api.errors.TaskRefusedParameter;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class CAPErrorMessageTaskRefusedImpl extends EnumeratedСAPErrorMessage1Impl implements CAPErrorMessageTaskRefused {
	protected CAPErrorMessageTaskRefusedImpl(TaskRefusedParameter taskRefusedParameter) {
        super((long) CAPErrorCode.taskRefused);

        if(taskRefusedParameter!=null)
        	setValue(Long.valueOf(taskRefusedParameter.getCode()));
    }

    public CAPErrorMessageTaskRefusedImpl() {
        super((long) CAPErrorCode.taskRefused);
    }

    public boolean isEmTaskRefused() {
        return true;
    }

    public CAPErrorMessageTaskRefused getEmTaskRefused() {
        return this;
    }

    public TaskRefusedParameter getTaskRefusedParameter() {
    	Long value=getValue();
    	if(value==null)
    		return null;
    	
    	return TaskRefusedParameter.getInstance(value.intValue());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("CAPErrorMessageTaskRefused [");
        TaskRefusedParameter taskRefusedParameter=getTaskRefusedParameter();
        if (taskRefusedParameter != null) {
            sb.append("taskRefusedParameter=");
            sb.append(taskRefusedParameter);
            sb.append(",");
        }
        sb.append("]");

        return sb.toString();
    }
}
