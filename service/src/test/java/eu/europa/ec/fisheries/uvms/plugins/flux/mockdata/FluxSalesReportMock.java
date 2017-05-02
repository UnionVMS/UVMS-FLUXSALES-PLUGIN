/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.plugins.flux.mockdata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import un.unece.uncefact.data.standard.fluxsalesreportmessage._3.FLUXSalesReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXPartyType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocumentType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;
import xeu.bridge_connector.v1.RequestType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.dom.DOMResult;

/**
 *
 * @author jojoha
 */
public class FluxSalesReportMock {

    public static RequestType mapToRequestType() throws JAXBException {
        RequestType requestType = new RequestType();
        requestType.setAny(mapToElement());
        return requestType;
    }

    private static Element mapToElement() throws JAXBException {
        FLUXSalesReportMessage attr = mapToFLUXReportDocumentType();
        JAXBContext context = JAXBContext.newInstance(FLUXSalesReportMessage.class);
        Marshaller marshaller = context.createMarshaller();
        DOMResult res = new DOMResult();
        marshaller.marshal(attr, res);
        return ((Document) res.getNode()).getDocumentElement();
    }

    private static FLUXSalesReportMessage mapToFLUXReportDocumentType() {
        FLUXSalesReportMessage message = new FLUXSalesReportMessage();
        message.setFLUXReportDocument(mapToFluxDocumentType());
        return message;
    }

    private static FLUXReportDocumentType mapToFluxDocumentType() {
        FLUXReportDocumentType message = new FLUXReportDocumentType();
        message.getIDS().add(mapToIDType(MockConstants.GUID_ID));
        message.setCreationDateTime(mapToDateTimeTypeNow());
        message.setPurposeCode(mapToCodeType(MockConstants.PURPOSE_CODE));
        message.setOwnerFLUXParty(mapToOwnerFluxParty());
        return message;
    }

    private static IDType mapToIDType(String value) {
        IDType type = new IDType();
        type.setValue(value);
        return type;
    }

    private static DateTimeType mapToDateTimeTypeNow() {
        DateTimeType dateTime = new DateTimeType();
        dateTime.setDateTime(MockConstants.NOW_DATE_JODA);
        return dateTime;
    }

    private static CodeType mapToCodeType(String value) {
        CodeType code = new CodeType();
        code.setValue(value);
        return code;
    }

    private static FLUXPartyType mapToOwnerFluxParty() {
        FLUXPartyType party = new FLUXPartyType();
        party.getNames().add(mapToTextType());
        return party;
    }

    private static TextType mapToTextType() {
        TextType tt = new TextType();
        tt.setValue(MockConstants.FLUX_OWNER);
        return tt;
    }


}
