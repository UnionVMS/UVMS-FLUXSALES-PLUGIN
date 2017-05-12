package eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper;


import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
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
public class FLUXSalesResponseMessageMapper {

    @Inject
    private MapperFacade mapper;

    public String mapToSalesResponseString(RequestType request) throws MappingException {
        try {
            un.unece.uncefact.data.standard.fluxsalesresponsemessage._3.FLUXSalesResponseMessage fluxSalesResponseMessage = unpackFluxSalesResponseMessage(request);

            FLUXSalesResponseMessage mappedFluxSalesReportMessage = mapper.map(fluxSalesResponseMessage, FLUXSalesResponseMessage.class);

            return JAXBMarshaller.marshallJaxBObjectToString(mappedFluxSalesReportMessage);
        } catch (SalesMarshallException | JAXBException e) {
            throw new MappingException("Could not map sales report to a string", e);
        }
    }

    private un.unece.uncefact.data.standard.fluxsalesresponsemessage._3.FLUXSalesResponseMessage unpackFluxSalesResponseMessage(RequestType request) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(un.unece.uncefact.data.standard.fluxsalesresponsemessage._3.FLUXSalesResponseMessage.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (un.unece.uncefact.data.standard.fluxsalesresponsemessage._3.FLUXSalesResponseMessage) unmarshaller.unmarshal(request.getAny());
    }
}
