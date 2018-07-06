package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.bean.helper;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;
import xeu.bridge_connector.v1.Connector2BridgeRequest;

import javax.ejb.Stateless;
import javax.xml.namespace.QName;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

import static org.apache.commons.lang3.Validate.notBlank;

@Stateless
@Slf4j
public class Connector2BridgeRequestHelper {

    public String determineMessageType(Connector2BridgeRequest request) {
        return request.getAny().getLocalName();
    }

    public String getFRPropertyOrException(Connector2BridgeRequest request) {
        String fr = request.getOtherAttributes().get(QName.valueOf("FR"));

        // FR is optional in the Connector2BridgeRequest, but the ExchangeModuleRequestMapper can't handle nulls.
        notBlank(fr, "FR value was null or blank");

        if (fr.contains(":")) {
            int indexOfColon = fr.indexOf(':');
            return fr.substring(0, indexOfColon);
        } else {
            return fr;
        }
    }

    public String getONPropertyOrNull(Connector2BridgeRequest request) {
        return request.getON();
    }

    public String getContentAsString(Connector2BridgeRequest request) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(request.getAny());

        transformer.transform(source, result);

        return result.getWriter().toString();
    }
}
