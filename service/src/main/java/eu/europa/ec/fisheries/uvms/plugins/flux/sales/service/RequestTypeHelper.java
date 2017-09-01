package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service;

import xeu.bridge_connector.v1.Connector2BridgeRequest;

import javax.ejb.Stateless;

@Stateless
public class RequestTypeHelper {

    public String determineMessageType(Connector2BridgeRequest requestType) {
        return requestType.getAny().getLocalName();
    }
}
