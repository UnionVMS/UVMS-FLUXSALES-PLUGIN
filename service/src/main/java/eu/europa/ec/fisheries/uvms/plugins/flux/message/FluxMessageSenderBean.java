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

import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.plugins.flux.PortInitiator;
import eu.europa.ec.fisheries.uvms.plugins.flux.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.exception.PluginException;
import eu.europa.ec.fisheries.uvms.plugins.flux.mapper.FluxMessageRequestMapper;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import xeu.connector_bridge.wsdl.v1.BridgeConnectorPortType;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.LocalBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xeu.connector_bridge.v1.PostMsgOutType;
import xeu.connector_bridge.v1.PostMsgType;

/**
 *
 */
@LocalBean
@Stateless
public class FluxMessageSenderBean {

    @EJB
    PortInitiator port;

    @EJB
    FluxMessageRequestMapper mapper;

    @EJB
    StartupBean startupBean;

    private static Logger LOG = LoggerFactory.getLogger(FluxMessageSenderBean.class);

    public String sendMovement(MovementType movement, String messageId) throws PluginException {
        try {

            LOG.info("Sending message to EU [ {} ] with messageID: {} ", messageId);

            BridgeConnectorPortType portType = port.getPort();

            //TODO Addd these in properties table
            Map<String, String> headerValues = new HashMap<>();

            String headerKey = startupBean.getSetting("CLIENT_CERT_HEADER");
            String headerValue = startupBean.getSetting("CLIENT_CERT_USER");

            headerValues.put(headerKey, headerValue);
            mapper.addHeaderValueToRequest(portType, headerValues);

            PostMsgType request = mapper.mapToRequest(movement, messageId);
            PostMsgOutType resp = portType.post(request);

            if (resp.getAssignedON() == null) {
                LOG.info("Failed to send to flux ", messageId);
            } else {
                LOG.info("Success when sending to flux ", messageId);
            }

            if (request.getID() != null && !request.getID().isEmpty()) {
                return request.getID();
            } else {
                throw new PluginException("No MessageID in request, MessageID must be set!");
            }

        } catch (Exception e) {
            LOG.error("[ Error when sending movement to FLUX. ] {}", e.getMessage());
            throw new PluginException(e.getMessage());
        }
    }

}
