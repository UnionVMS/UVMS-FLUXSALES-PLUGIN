package eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.mock;

import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.PortInitiator;
import xeu.connector_bridge.wsdl.v1.BridgeConnectorPortType;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class PortInitiatorMock implements PortInitiator {

    @EJB
    private PortMock port;

    @Override
    public BridgeConnectorPortType getPort() {
        return port;
    }

    @Override
    public void updatePort() {
        //stub
    }

}
