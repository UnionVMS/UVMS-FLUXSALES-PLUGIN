package eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesQueryMessage;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.exception.MappingException;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import ma.glasnost.orika.MapperFacade;
import xeu.bridge_connector.v1.RequestType;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@Stateless
public class FLUXSalesQueryMessageMapper {

    @Inject
    private MapperFacade mapper;

    public String mapToSalesQueryString(RequestType request) throws MappingException {
        try {
            un.unece.uncefact.data.standard.fluxsalesquerymessage._3.FLUXSalesQueryMessage fluxSalesQueryMessage = unpackFluxSalesQueryMessage(request);


            FLUXSalesQueryMessage mappedFluxSalesReportMessage = mapper.map(fluxSalesQueryMessage, FLUXSalesQueryMessage.class);

            return JAXBMarshaller.marshallJaxBObjectToString(mappedFluxSalesReportMessage);
        } catch (SalesMarshallException | JAXBException e) {
            throw new MappingException("Could not map sales query to a string", e);
        }
    }


    private un.unece.uncefact.data.standard.fluxsalesquerymessage._3.FLUXSalesQueryMessage unpackFluxSalesQueryMessage(RequestType request) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(un.unece.uncefact.data.standard.fluxsalesquerymessage._3.FLUXSalesQueryMessage.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (un.unece.uncefact.data.standard.fluxsalesquerymessage._3.FLUXSalesQueryMessage) unmarshaller.unmarshal(request.getAny());
    }
}