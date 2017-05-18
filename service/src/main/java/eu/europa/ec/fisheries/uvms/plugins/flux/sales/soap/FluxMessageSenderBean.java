/*
 ﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 © European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.plugins.flux.sales.soap;

import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesReportRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesResponseRequest;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.PortInitiator;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.exception.MappingException;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.exception.PluginException;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper.PostMsgTypeMapper;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xeu.connector_bridge.v1.PostMsgOutType;
import xeu.connector_bridge.v1.PostMsgType;
import xeu.connector_bridge.wsdl.v1.BridgeConnectorPortType;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@LocalBean
@Stateless
public class FluxMessageSenderBean {

    public static final String SALES_DF = "urn:un:unece:uncefact:fisheries:FLUX:SALES:EU:2";

    @EJB
    private PortInitiator port;

    @EJB
    private StartupBean startupBean;

    @EJB
    private PostMsgTypeMapper postMsgTypeMapper;

    @Inject
    private MapperFacade mapper;

    private static final Logger LOG = LoggerFactory.getLogger(FluxMessageSenderBean.class);


    public String sendSalesReportToFlux(SendSalesReportRequest salesReport) throws PluginException, SalesMarshallException {
        try {
            un.unece.uncefact.data.standard.fluxsalesreportmessage._3.FLUXSalesReportMessage marshalledResponse = unmarshalAndMapToUNCEFACTFluxSalesReportMessage(salesReport);
            String recipient = salesReport.getRecipient();

            PostMsgType request = postMsgTypeMapper.wrapInPostMsgType(marshalledResponse, SALES_DF, recipient);
            return sendPostMsgType(request, recipient);
        } catch (MappingException e) {
            LOG.error("[ Error when sending sales report to FLUX. ] {}", e.getMessage());
            throw new PluginException(e);
        }
    }

    public String sendSalesResponseToFlux(SendSalesResponseRequest salesResponse) throws SalesMarshallException, PluginException {
        try {
            un.unece.uncefact.data.standard.fluxsalesresponsemessage._3.FLUXSalesResponseMessage marshalledResponse = unmarshalAndMapToUNCEFACTFluxSalesResponseMessage(salesResponse);
            String recipient = salesResponse.getRecipient();

            PostMsgType postMsgType = postMsgTypeMapper.wrapInPostMsgType(marshalledResponse, SALES_DF, recipient);
            return sendPostMsgType(postMsgType, recipient);
        } catch (PluginException | MappingException e) {
            LOG.error("[ Error when sending sales response to FLUX. ] {}", e.getMessage());
            throw new PluginException(e);
        }
    }

    private un.unece.uncefact.data.standard.fluxsalesresponsemessage._3.FLUXSalesResponseMessage unmarshalAndMapToUNCEFACTFluxSalesResponseMessage(SendSalesResponseRequest request) throws SalesMarshallException {
        eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage fluxSalesResponseMessage = JAXBMarshaller.unmarshallString(request.getResponse(), FLUXSalesResponseMessage.class);
        return mapper.map(fluxSalesResponseMessage, un.unece.uncefact.data.standard.fluxsalesresponsemessage._3.FLUXSalesResponseMessage.class);
    }

    private un.unece.uncefact.data.standard.fluxsalesreportmessage._3.FLUXSalesReportMessage unmarshalAndMapToUNCEFACTFluxSalesReportMessage(SendSalesReportRequest request) throws SalesMarshallException {
        eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage fluxSalesResponseMessage = JAXBMarshaller.unmarshallString(request.getReport(), FLUXSalesReportMessage.class);
        return mapper.map(fluxSalesResponseMessage, un.unece.uncefact.data.standard.fluxsalesreportmessage._3.FLUXSalesReportMessage.class);
    }

    public String sendPostMsgType(PostMsgType request, String recipient) throws PluginException {
        try {

            LOG.info("Sending message to EU [ {} ] with messageID: {} ", request.getID());

            BridgeConnectorPortType portType = port.getPort();

            //TODO Add these in properties table
            Map<String, String> headerValues = new HashMap<>();

            String headerKey = startupBean.getSetting("CLIENT_CERT_HEADER");
            String headerValue = startupBean.getSetting("CLIENT_CERT_USER");

            headerValues.put(headerKey, headerValue);
            postMsgTypeMapper.addHeaderValueToRequest(portType, headerValues);

            PostMsgOutType resp = portType.post(request);

            if (resp.getAssignedON() == null) {
                LOG.info("Failed to send PostMsgType to flux Recipient {}, Message id {} Should correlate with the GUID of the sent report", recipient, request.getID());
            } else {
                LOG.info("Success when sending to flux MessageId {} ( Should correlate with the GUID of the sent report ) Recipient {} ", request.getID(), recipient);
            }

            if (request.getID() != null && !request.getID().isEmpty()) {
                return request.getID();
            } else {
                throw new PluginException("No MessageID in request, MessageID must be set!");
            }

        } catch (Exception e) {
            LOG.error("[ Error when sending a message to FLUX. ] {}", e.getMessage());
            throw new PluginException(e);
        }
    }

}
