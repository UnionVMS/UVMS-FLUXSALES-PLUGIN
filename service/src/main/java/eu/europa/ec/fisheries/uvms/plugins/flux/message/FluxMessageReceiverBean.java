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
package eu.europa.ec.fisheries.uvms.plugins.flux.message;

import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
import eu.europa.ec.fisheries.uvms.plugins.flux.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.constants.FluxDataFlowName;
import eu.europa.ec.fisheries.uvms.plugins.flux.exception.PluginException;
import eu.europa.ec.fisheries.uvms.plugins.flux.mapper.FLUXSalesQueryMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.mapper.FLUXSalesReportMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.mapper.FluxMessageResponseMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.mapper.MappingException;
import eu.europa.ec.fisheries.uvms.plugins.flux.service.ExchangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xeu.bridge_connector.v1.RequestType;
import xeu.bridge_connector.v1.ResponseType;
import xeu.bridge_connector.wsdl.v1.BridgeConnectorPortType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.bind.JAXBException;
import java.util.List;

/**
 *
 */
@Stateless
@WebService(serviceName = "MovementService", targetNamespace = "urn:xeu:bridge-connector:wsdl:v1", portName = "BridgeConnectorPortType", endpointInterface = "xeu.bridge_connector.wsdl.v1.BridgeConnectorPortType")
public class FluxMessageReceiverBean implements BridgeConnectorPortType {

    private static final Logger LOG = LoggerFactory.getLogger(FluxMessageReceiverBean.class);

    @EJB
    private ExchangeService exchange;

    @EJB
    private StartupBean startupBean;

    @EJB
    private FLUXSalesReportMessageMapper fluxSalesReportMessageMapper;

    @EJB
    private FLUXSalesQueryMessageMapper fluxSalesQueryMessageMapper;

    @Override
    public ResponseType post(RequestType request) {

        ResponseType type = new ResponseType();
        if (!startupBean.isIsEnabled()) {
            type.setStatus("NOK");
            return type;
        }

        try {
            switch (request.getDF()) {
                case FluxDataFlowName.VESSEL_POSITION:
                    receiveVesselPosition(request);
                    break;
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

            type.setStatus("OK");
            return type;
        } catch (Exception e) {
            LOG.error("[ Error when receiving data from FLUX. ]", e);
            type.setStatus("NOK");
            return type;
        }
    }

    private void receiveVesselPosition(RequestType request) throws JAXBException, PluginException {
        LOG.debug("Got position report request from FLUX in FLUX plugin");
        List<SetReportMovementType> movements = FluxMessageResponseMapper.mapToMovementType(request, startupBean.getRegisterClassName());

        for (SetReportMovementType movement : movements) {
            exchange.sendMovementReportToExchange(movement);
        }
    }

    private void receiveSalesResponse(RequestType request) throws MappingException {
        LOG.debug("Got sales report from FLUX in FLUX plugin");
        String report = fluxSalesReportMessageMapper.mapToSalesResponseString(request);
        exchange.sendSalesResponseToExchange(report);
    }

    private void receiveSalesReport(RequestType request) throws MappingException, PluginException {
        LOG.debug("Got sales report from FLUX in FLUX plugin");
        String report = fluxSalesReportMessageMapper.mapToSalesReportString(request);
        exchange.sendSalesReportToExchange(report);
    }

    private void receiveSalesQuery(RequestType request) throws MappingException, PluginException {
        LOG.debug("Got sales report from FLUX in FLUX plugin");
        String salesQuery = fluxSalesQueryMessageMapper.mapToSalesQueryString(request);
        exchange.sendSalesQueryToExchange(salesQuery);
    }

}
