package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.mapper;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesQueryMessage;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.exception.MappingException;
import ma.glasnost.orika.MapperFacade;
import xeu.bridge_connector.v1.Connector2BridgeRequest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@Stateless
public class FLUXSalesQueryMessageMapper {

    @Inject
    private MapperFacade mapper;

    public FLUXSalesQueryMessage mapToSalesQuery(Connector2BridgeRequest request) throws MappingException {
        try {
            eu.europa.ec.fisheries.schema.flux.sales.FLUXSalesQueryMessage fluxSalesQueryMessage = unpackFluxSalesQueryMessage(request);
            return mapper.map(fluxSalesQueryMessage, FLUXSalesQueryMessage.class);
        } catch (JAXBException e) {
            throw new MappingException("Could not unmarshall the supplied xml for a sales query.", e);
        }
    }


    private eu.europa.ec.fisheries.schema.flux.sales.FLUXSalesQueryMessage unpackFluxSalesQueryMessage(Connector2BridgeRequest request) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(eu.europa.ec.fisheries.schema.flux.sales.FLUXSalesQueryMessage.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (eu.europa.ec.fisheries.schema.flux.sales.FLUXSalesQueryMessage) unmarshaller.unmarshal(request.getAny());
    }
}
