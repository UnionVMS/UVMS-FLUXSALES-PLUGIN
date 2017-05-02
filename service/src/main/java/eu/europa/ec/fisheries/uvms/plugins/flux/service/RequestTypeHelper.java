package eu.europa.ec.fisheries.uvms.plugins.flux.service;

import xeu.bridge_connector.v1.RequestType;

import javax.ejb.Stateless;

@Stateless
public class RequestTypeHelper {

    public String determineMessageType(RequestType requestType) {
        return requestType.getAny().getLocalName();
    }
}
