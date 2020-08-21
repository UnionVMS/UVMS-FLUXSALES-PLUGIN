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
package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.consumer;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.PluginBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesReportRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesResponseRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetConfigRequest;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.bean.PluginService;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.exception.PluginException;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.producer.ExchangeEventMessageProducerBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.soap.FluxMessageSenderBean;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.*;

@MessageDriven(mappedName = ExchangeModelConstants.PLUGIN_EVENTBUS, activationConfig = {
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = ExchangeModelConstants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = ExchangeModelConstants.DESTINATION_TYPE_TOPIC),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = ExchangeModelConstants.EVENTBUS_NAME),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "eu.europa.ec.fisheries.uvms.plugins.flux.sales"),
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "eu.europa.ec.fisheries.uvms.plugins.flux.sales"),
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "ServiceName='eu.europa.ec.fisheries.uvms.plugins.flux.sales'"),
    @ActivationConfigProperty(propertyName = "initialRedeliveryDelay", propertyValue = "60000"),
    @ActivationConfigProperty(propertyName = "maximumRedeliveries", propertyValue = "3")
})
@Slf4j
public class FluxSalesPluginListener implements MessageListener {

    static final Logger LOG = LoggerFactory.getLogger(FluxSalesPluginListener.class);

    @EJB
    private PluginService service;

    @EJB
    private FluxMessageSenderBean senderToFLUX;

    @EJB
    private ExchangeEventMessageProducerBean messageProducer;

    @EJB
    private StartupBean startup;


    @Override
    public void onMessage(Message inMessage) {

        LOG.debug("FluxSalesPluginListener (MessageConstants.PLUGIN_SERVICE_CLASS_NAME): {}", startup.getRegisterClassName());

        TextMessage textMessage = (TextMessage) inMessage;
        MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(textMessage);
        try {
            PluginBaseRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, PluginBaseRequest.class);

            String responseMessage = null;

            log.info("Received method:" + request.getMethod());

            switch (request.getMethod()) {
                case SET_CONFIG:
                    SetConfigRequest setConfigRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, SetConfigRequest.class);
                    AcknowledgeTypeType setConfig = service.setConfig(setConfigRequest.getConfigurations());
                    AcknowledgeType setConfigAck = ExchangePluginResponseMapper.mapToAcknowledgeType(textMessage.getJMSMessageID(), setConfig);
                    responseMessage = ExchangePluginResponseMapper.mapToSetConfigResponse(startup.getRegisterClassName(), setConfigAck);
                    break;
                case START:
                    AcknowledgeTypeType start = service.start();
                    AcknowledgeType startAck = ExchangePluginResponseMapper.mapToAcknowledgeType(textMessage.getJMSMessageID(), start);
                    responseMessage = ExchangePluginResponseMapper.mapToStartResponse(startup.getRegisterClassName(), startAck);
                    break;
                case STOP:
                    AcknowledgeTypeType stop = service.stop();
                    AcknowledgeType stopAck = ExchangePluginResponseMapper.mapToAcknowledgeType(textMessage.getJMSMessageID(), stop);
                    responseMessage = ExchangePluginResponseMapper.mapToStopResponse(startup.getRegisterClassName(), stopAck);
                    break;
                case PING:
                    responseMessage = ExchangePluginResponseMapper.mapToPingResponse(startup.isEnabled(), startup.isEnabled());
                    break;
                case SEND_SALES_RESPONSE:
                    SendSalesResponseRequest salesResponse = JAXBMarshaller.unmarshallTextMessage(textMessage, SendSalesResponseRequest.class);
                    senderToFLUX.sendSalesResponseToFlux(salesResponse);
                    break;
                case SEND_SALES_REPORT:
                    SendSalesReportRequest salesReportRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, SendSalesReportRequest.class);
                    senderToFLUX.sendSalesReportToFlux(salesReportRequest);
                    break;
                default:
                    LOG.error("Not supported method");
                    break;
            }

            if (responseMessage != null) {
                messageProducer.sendResponseMessageToSender(textMessage, responseMessage, 60000, DeliveryMode.NON_PERSISTENT);
            }

        } catch (ExchangeModelMarshallException | NullPointerException e) {
            LOG.error("Error when receiving message in flux " + startup.getRegisterClassName() , e);
        } catch (JMSException | MessageException | PluginException | SalesMarshallException ex) {
            LOG.error("Error when handling JMS message in flux " + startup.getRegisterClassName(), ex);
        }
    }
}