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
package eu.europa.ec.fisheries.uvms.plugins.flux.service;

import eu.europa.ec.fisheries.schema.exchange.common.v1.*;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SalesMessageResponse;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.uvms.plugins.flux.PortInitiator;
import eu.europa.ec.fisheries.uvms.plugins.flux.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.exception.PluginException;
import eu.europa.ec.fisheries.uvms.plugins.flux.mapper.FLUXSalesReportMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.message.FluxMessageSenderBean;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.UUID;

/**
 *
 */
@LocalBean
@Stateless
public class PluginService {

    @EJB
    StartupBean startupBean;

    @EJB
    FluxMessageSenderBean sender;

    @EJB
    PortInitiator portInintiator;


    @EJB
    private FLUXSalesReportMessageMapper fluxSalesReportMessageMapper;

    final static Logger LOG = LoggerFactory.getLogger(PluginService.class);

    /**
     * TODO implement
     *
     * @param report
     * @return
     */
    public AcknowledgeTypeType setReport(ReportType report) {
        LOG.info(startupBean.getRegisterClassName() + ".report(" + report.getType().name() + ")");
        LOG.debug("timestamp: " + report.getTimestamp());

            try {
            if (report.getMovement() != null && ReportTypeType.MOVEMENT.equals(report.getType())) {
                sendMovement(report);
            } else if (report.getSalesReport() != null && ReportTypeType.SALES.equals(report.getType())) {
                sendSalesReport(report);
            }
        } catch (PluginException ex) {
            LOG.debug("Error when setting report");
            return AcknowledgeTypeType.NOK;
        }

        return AcknowledgeTypeType.OK;
    }


    private void sendMovement(ReportType report) throws PluginException {
        MovementType movement = report.getMovement();
                MovementPoint pos = movement.getPosition();
                if (pos != null) {
                    LOG.info("lon: " + pos.getLongitude());
                    LOG.info("lat: " + pos.getLatitude());
                }

                String editorType = startupBean.getSetting("EDITOR_TYPE");
                String actionReason = startupBean.getSetting("ACTION_REASON");

                String messageId = UUID.randomUUID().toString();
                if (movement.getGuid() != null) {
                    messageId = movement.getGuid();
                }

                sender.sendMovement(movement, messageId, report.getRecipient());
    }

    private void sendSalesReport(ReportType report) throws PluginException {
        String messageId = UUID.randomUUID().toString();
        sender.sendSalesReport(report.getSalesReport(), messageId, report.getRecipient());
            }

    public void sendSalesQuery(SalesMessageResponse salesQueryResponse) throws PluginException, SalesMarshallException {
        String messageId = UUID.randomUUID().toString();
        sender.sendSalesQuery(salesQueryResponse, messageId);
    }

    /**
     * TODO implement
     *
     * @param command
     * @return
     */
    public AcknowledgeTypeType setCommand(CommandType command) {
        LOG.info(startupBean.getRegisterClassName() + ".setCommand(" + command.getCommand().name() + ")");
        LOG.debug("timestamp: " + command.getTimestamp());
        PollType poll = command.getPoll();
        EmailType email = command.getEmail();
        if (poll != null && CommandTypeType.POLL.equals(command.getCommand())) {
            LOG.info("POLL: " + poll.getPollId());
        }
        if (email != null && CommandTypeType.EMAIL.equals(command.getCommand())) {
            LOG.info("EMAIL: subject=" + email.getSubject());
        }
        return AcknowledgeTypeType.OK;
    }

    /**
     * Set the config values for the flux
     *
     * @param settings
     * @return
     */
    public AcknowledgeTypeType setConfig(SettingListType settings) {
        LOG.info(startupBean.getRegisterClassName() + ".setConfig()");
        try {
            for (KeyValueType values : settings.getSetting()) {
                LOG.debug("Setting [ " + values.getKey() + " : " + values.getValue() + " ]");
                startupBean.getSettings().put(values.getKey(), values.getValue());
            }
            portInintiator.updatePort();
            return AcknowledgeTypeType.OK;
        } catch (Exception e) {
            LOG.error("Failed to set config in {}", startupBean.getRegisterClassName());
            return AcknowledgeTypeType.NOK;
        }

    }

    /**
     * Start the flux. Use this to enable functionality in the flux
     *
     * @return
     */
    public AcknowledgeTypeType start() {
        LOG.info(startupBean.getRegisterClassName() + ".start()");
        try {
            startupBean.setIsEnabled(Boolean.TRUE);
            return AcknowledgeTypeType.OK;
        } catch (Exception e) {
            startupBean.setIsEnabled(Boolean.FALSE);
            LOG.error("Failed to start {}", startupBean.getRegisterClassName());
            return AcknowledgeTypeType.NOK;
        }

    }

    /**
     * Stop the flux. Use this to disable functionality in the flux
     *
     * @return
     */
    public AcknowledgeTypeType stop() {
        LOG.info(startupBean.getRegisterClassName() + ".stop()");
        try {
            startupBean.setIsEnabled(Boolean.FALSE);
            return AcknowledgeTypeType.OK;
        } catch (Exception e) {
            startupBean.setIsEnabled(Boolean.TRUE);
            LOG.error("Failed to stop {}", startupBean.getRegisterClassName());
            return AcknowledgeTypeType.NOK;
        }
    }

    public AcknowledgeTypeType handleIncomingResponse(String message) {
        try {
            FLUXSalesResponseMessage fluxSalesResponseMessage = JAXBMarshaller.unmarshallString(message, FLUXSalesResponseMessage.class);
        } catch (SalesMarshallException e) {
            LOG.error("Failed to marshal FLUXSalesResponseMessage", e);
            return AcknowledgeTypeType.NOK;
        }
        return AcknowledgeTypeType.OK;
    }
}
