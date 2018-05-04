package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.mapper;


import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.exception.MappingException;
import ma.glasnost.orika.MapperFacade;
import xeu.bridge_connector.v1.Connector2BridgeRequest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@Stateless
public class FLUXSalesResponseMessageMapper {

    @Inject
    private MapperFacade mapper;

    public FLUXSalesResponseMessage mapToSalesResponse(Connector2BridgeRequest request) throws MappingException {
        try {
            eu.europa.ec.fisheries.schema.flux.sales.FLUXSalesResponseMessage fluxSalesResponseMessage = unpackFluxSalesResponseMessage(request);

            return mapper.map(fluxSalesResponseMessage, FLUXSalesResponseMessage.class);
        } catch (JAXBException e) {
            throw new MappingException("Could not unmarshall the supplied xml for a sales query.", e);
        }
    }

    private eu.europa.ec.fisheries.schema.flux.sales.FLUXSalesResponseMessage unpackFluxSalesResponseMessage(Connector2BridgeRequest request) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(eu.europa.ec.fisheries.schema.flux.sales.FLUXSalesResponseMessage.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (eu.europa.ec.fisheries.schema.flux.sales.FLUXSalesResponseMessage) unmarshaller.unmarshal(request.getAny());
    }
}
