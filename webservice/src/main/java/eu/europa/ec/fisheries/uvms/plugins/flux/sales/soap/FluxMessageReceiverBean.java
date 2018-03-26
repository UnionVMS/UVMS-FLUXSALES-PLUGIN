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

import eu.europa.ec.fisheries.schema.sales.FLUXSalesQueryMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.constants.FluxDataFlowName;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.exception.MappingException;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.exception.PluginException;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper.FLUXSalesQueryMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper.FLUXSalesReportMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper.FLUXSalesResponseMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.ValidationService;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.XsdValidatorService;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.helper.Connector2BridgeRequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.xmlunit.validation.ValidationResult;
import xeu.bridge_connector.v1.Connector2BridgeRequest;
import xeu.bridge_connector.v1.Connector2BridgeResponse;
import xeu.bridge_connector.wsdl.v1.BridgeConnectorPortType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;
import java.util.UUID;

/**
 * This is the entry point for incoming FLUX messages.
 */
@Stateless
@WebService(serviceName = "SalesService", targetNamespace = "urn:xeu:bridge-connector:wsdl:v1", portName = "BridgeConnectorPortType", endpointInterface = "xeu.bridge_connector.wsdl.v1.BridgeConnectorPortType")
public class FluxMessageReceiverBean implements BridgeConnectorPortType {

    private static final Logger LOG = LoggerFactory.getLogger(FluxMessageReceiverBean.class);

    @EJB
    private ExchangeService exchange;

    @EJB
    private XsdValidatorService xsdValidatorService;

    @EJB
    private ValidationService validationService;

    @EJB
    private StartupBean startupBean;

    @EJB
    private FLUXSalesReportMessageMapper fluxSalesReportMessageMapper;

    @EJB
    private FLUXSalesQueryMessageMapper fluxSalesQueryMessageMapper;

    @EJB
    private FLUXSalesResponseMessageMapper fluxSalesResponseMessageMapper;

    @EJB
    private Connector2BridgeRequestHelper requestHelper;

    @Override
    public Connector2BridgeResponse post(Connector2BridgeRequest request) {
        MDC.put("requestId", UUID.randomUUID().toString());
        LOG.info("Received request message");
        Connector2BridgeResponse response = new Connector2BridgeResponse();
        if (!startupBean.isEnabled()) {
            response.setStatus("NOK");
            return response;
        }

        ValidationResult validationResult = xsdValidatorService.doesMessagePassXsdValidation(request.getAny());

        if (!validationResult.isValid()) {
            validationService.sendMessageToSales(request, validationResult.getProblems());
            response.setStatus("OK");
            return response;
        }

        try {
            switch (requestHelper.determineMessageType(request)) {
                case FluxDataFlowName.SALES_REPORT:
                    receiveSalesReport(request);
                    break;
                case FluxDataFlowName.SALES_QUERY:
                    receiveSalesQuery(request);
                    break;
                case FluxDataFlowName.SALES_RECEIVE_RESPONSE:
                    receiveSalesResponse(request);
                    break;
                default: throw new PluginException("In the FLUX plugin, no action is defined for a FLUX request with DF " + request.getDF());
            }

            response.setStatus("OK");
            return response;
        } catch (Exception e) {
            LOG.error("[ Error when receiving data from FLUX. ]", e);
            response.setStatus("NOK");
            return response;
        }
    }

    private void receiveSalesResponse(Connector2BridgeRequest request) throws MappingException {
        LOG.debug("Got sales response from FLUX in FLUX plugin");
        FLUXSalesResponseMessage fluxSalesResponseMessage = fluxSalesResponseMessageMapper.mapToSalesResponse(request);
        String fr = requestHelper.getFRPropertyOrNull(request);
        String on = requestHelper.getONPropertyOrNull(request);
        exchange.sendSalesResponseToExchange(fluxSalesResponseMessage, fr, on);
    }

    private void receiveSalesReport(Connector2BridgeRequest request) throws MappingException, PluginException {
        LOG.debug("Got sales report from FLUX in FLUX plugin");
        Report report = fluxSalesReportMessageMapper.mapToReport(request);
        String fr = requestHelper.getFRPropertyOrNull(request);
        String on = requestHelper.getONPropertyOrNull(request);
        exchange.sendSalesReportToExchange(report, fr, on);
    }

    private void receiveSalesQuery(Connector2BridgeRequest request) throws MappingException, PluginException {
        LOG.debug("Got sales query from FLUX in FLUX plugin");
        FLUXSalesQueryMessage salesQuery = fluxSalesQueryMessageMapper.mapToSalesQuery(request);
        String fr = requestHelper.getFRPropertyOrNull(request);
        String on = requestHelper.getONPropertyOrNull(request);
        exchange.sendSalesQueryToExchange(salesQuery, fr, on);
    }

}
