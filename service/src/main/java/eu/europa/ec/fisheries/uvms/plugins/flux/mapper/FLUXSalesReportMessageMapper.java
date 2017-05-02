package eu.europa.ec.fisheries.uvms.plugins.flux.mapper;


import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.plugins.flux.StartupBean;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import ma.glasnost.orika.MapperFacade;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xeu.bridge_connector.v1.RequestType;
import xeu.connector_bridge.v1.PostMsgType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMResult;

@Stateless
public class FLUXSalesReportMessageMapper {

    @EJB
    private StartupBean settings;

    @Inject
    private MapperFacade mapper;

    public String mapToSalesReportString(RequestType request) throws MappingException {
        try {
            un.unece.uncefact.data.standard.fluxsalesreportmessage._3.FLUXSalesReportMessage fluxSalesReportMessage = unpackFluxSalesReportMessage(request);

            FLUXSalesReportMessage mappedFluxSalesReportMessage = mapper.map(fluxSalesReportMessage, FLUXSalesReportMessage.class);

            Report report = new Report();
            report.setFLUXSalesReportMessage(mappedFluxSalesReportMessage);

            return JAXBMarshaller.marshallJaxBObjectToString(report);
        } catch (SalesMarshallException | JAXBException e) {
            throw new MappingException("Could not map sales report to a string", e);
        }
    }

    public String mapToSalesResponseString(RequestType request) throws MappingException {
        try {
            un.unece.uncefact.data.standard.fluxsalesresponsemessage._3.FLUXSalesResponseMessage fluxSalesResponseMessage = unpackFluxSalesResponseMessage(request);

            FLUXSalesResponseMessage mappedFluxSalesReportMessage = mapper.map(fluxSalesResponseMessage, FLUXSalesResponseMessage.class);

            return JAXBMarshaller.marshallJaxBObjectToString(mappedFluxSalesReportMessage);
        } catch (SalesMarshallException | JAXBException e) {
            throw new MappingException("Could not map sales report to a string", e);
        }
    }

    public PostMsgType mapToSalesReportPostMsgType(String salesReportAsString, String messageId, String recipient) throws MappingException {
        try {
            Report salesReport = JAXBMarshaller.unmarshallString(salesReportAsString, Report.class);
            PostMsgType message = new PostMsgType();

            if (recipient == null || recipient.isEmpty()) {
                message.setAD(settings.getSetting("FLUX_DEFAULT_AD"));
            } else {
                message.setAD(recipient);
            }

            message.setDF(settings.getSetting("FLUX_DATAFLOW"));
            message.setID(messageId);

            JAXBContext context = JAXBContext.newInstance(FLUXSalesReportMessage.class);
            Marshaller marshaller = context.createMarshaller();
            DOMResult res = new DOMResult();
            marshaller.marshal(salesReport.getFLUXSalesReportMessage(), res);

            Element elt = ((Document) res.getNode()).getDocumentElement();

            message.setAny(elt);
            return message;
        } catch (SalesMarshallException | JAXBException e) {
            throw new MappingException("Could not map sales report to post msg", e);
        }
    }

    private un.unece.uncefact.data.standard.fluxsalesreportmessage._3.FLUXSalesReportMessage unpackFluxSalesReportMessage(RequestType request) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(un.unece.uncefact.data.standard.fluxsalesreportmessage._3.FLUXSalesReportMessage.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (un.unece.uncefact.data.standard.fluxsalesreportmessage._3.FLUXSalesReportMessage) unmarshaller.unmarshal(request.getAny());
    }
    private un.unece.uncefact.data.standard.fluxsalesresponsemessage._3.FLUXSalesResponseMessage unpackFluxSalesResponseMessage(RequestType request) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(un.unece.uncefact.data.standard.fluxsalesresponsemessage._3.FLUXSalesResponseMessage.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (un.unece.uncefact.data.standard.fluxsalesresponsemessage._3.FLUXSalesResponseMessage) unmarshaller.unmarshal(request.getAny());
    }
}
