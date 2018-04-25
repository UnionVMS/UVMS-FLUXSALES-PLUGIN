package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.mapper;


import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.exception.MappingException;
import ma.glasnost.orika.MapperFacade;
import xeu.bridge_connector.v1.Connector2BridgeRequest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@Stateless
public class FLUXSalesReportMessageMapper {

    @Inject
    private MapperFacade mapper;

    public Report mapToReport(Connector2BridgeRequest request) throws MappingException {
        try {
            eu.europa.ec.fisheries.schema.flux.sales.FLUXSalesReportMessage fluxSalesReportMessage = unpackFluxSalesReportMessage(request);
            FLUXSalesReportMessage mappedFluxSalesReportMessage = mapper.map(fluxSalesReportMessage, FLUXSalesReportMessage.class);

            return new Report()
                    .withFLUXSalesReportMessage(mappedFluxSalesReportMessage);
        } catch (JAXBException e) {
            throw new MappingException("Could not unmarshall the supplied xml for a sales report", e);
        }
    }

    private eu.europa.ec.fisheries.schema.flux.sales.FLUXSalesReportMessage unpackFluxSalesReportMessage(Connector2BridgeRequest request) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(eu.europa.ec.fisheries.schema.flux.sales.FLUXSalesReportMessage.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (eu.europa.ec.fisheries.schema.flux.sales.FLUXSalesReportMessage) unmarshaller.unmarshal(request.getAny());
    }
}
