package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.RouteingNumber;
import org.restcomm.protocols.ss7.map.primitives.TbcdString;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
public class RouteingNumberImpl extends TbcdString implements RouteingNumber {
	private static final long serialVersionUID = 1L;

	public RouteingNumberImpl() {
        super(1, 5, "RouteingNumber");
    }

    public RouteingNumberImpl(String data) {
        super(1, 5, "RouteingNumber", data);
    }

    public String getRouteingNumber() {
        return data;
    }
}
