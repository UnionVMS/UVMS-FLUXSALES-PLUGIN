package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.helper;

import xeu.bridge_connector.v1.Connector2BridgeRequest;

import javax.ejb.Stateless;
import javax.xml.namespace.QName;

@Stateless
public class Connector2BridgeRequestHelper {

    public String determineMessageType(Connector2BridgeRequest request) {
        return request.getAny().getLocalName();
    }

    public String getFRPropertyOrNull(Connector2BridgeRequest request) {
        //return "FRA";
        return request.getOtherAttributes().get(QName.valueOf("FR"));
    }

    public String getONPropertyOrNull(Connector2BridgeRequest request) {
        return request.getON();
    }
}
