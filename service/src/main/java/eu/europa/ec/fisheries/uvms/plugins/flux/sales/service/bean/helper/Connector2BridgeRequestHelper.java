package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.bean.helper;

import xeu.bridge_connector.v1.Connector2BridgeRequest;

import javax.ejb.Stateless;
import javax.xml.namespace.QName;

import static org.apache.commons.lang3.Validate.notBlank;

@Stateless
public class Connector2BridgeRequestHelper {

    public String determineMessageType(Connector2BridgeRequest request) {
        return request.getAny().getLocalName();
    }

    public String getFRPropertyOrException(Connector2BridgeRequest request) {
        String fr = request.getOtherAttributes().get(QName.valueOf("FR"));

        // FR is optional in the Connector2BridgeRequest, but the ExchangeModuleRequestMapper can't handle nulls.
        notBlank(fr, "FR value was null or blank");

        if (fr.contains(":")){
            int indexOfColon = fr.indexOf(':');
            return fr.substring(0, indexOfColon);
        } else {
            return fr;
        }
    }

    public String getONPropertyOrNull(Connector2BridgeRequest request) {
        return request.getON();
    }
}
