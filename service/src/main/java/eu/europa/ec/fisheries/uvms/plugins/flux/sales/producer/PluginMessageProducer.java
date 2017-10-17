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
package eu.europa.ec.fisheries.uvms.plugins.flux.sales.producer;

import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.message.JMSUtils;
import eu.europa.ec.fisheries.uvms.message.MessageConstants;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.constants.ModuleQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.*;

@Stateless
@LocalBean
public class PluginMessageProducer {

    private Queue exchangeQueue;
    private Topic eventBus;

    private ConnectionFactory connectionFactory;

    final static Logger LOG = LoggerFactory.getLogger(PluginMessageProducer.class);
    final static long TIMEOUT = 60000L;

    @PostConstruct
    public void resourceLookup() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
        exchangeQueue = JMSUtils.lookupQueue(ExchangeModelConstants.EXCHANGE_MESSAGE_IN_QUEUE);
        eventBus = JMSUtils.lookupTopic(ExchangeModelConstants.PLUGIN_EVENTBUS);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendResponseMessage(String text, TextMessage originalJMSMessage) throws JMSException {
        Connection connection = null;
        try {

            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);
            TextMessage response = session.createTextMessage(text);
            response.setJMSCorrelationID(originalJMSMessage.getJMSMessageID());
            MessageProducer producer = getProducer(session, originalJMSMessage.getJMSReplyTo(), TIMEOUT);
            producer.send(response);

        } catch (JMSException e) {
            LOG.error("[ Error when returning module request. ] {}", e.getMessage()); //TODO: check error handling
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendModuleMessage(String text, ModuleQueue queue) throws JMSException {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);
            TextMessage jmsMessage = createJMSMessage(text, session);

            switch (queue) {
                case EXCHANGE:
                    getProducer(session, exchangeQueue, TIMEOUT).send(jmsMessage);
                    break;
                default:
                    throw new UnsupportedOperationException("FLUX-Sales plugin has no functionality implemented to talk with " + queue);
            }

            return jmsMessage.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending a message to " + queue + ". ]", e);
            throw new JMSException(e.getMessage());
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendEventBusMessage(String text, String serviceName) throws JMSException {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            TextMessage message = session.createTextMessage();
            message.setText(text);
            message.setStringProperty(ExchangeModelConstants.SERVICE_NAME, serviceName);

            getProducer(session, eventBus, TIMEOUT).send(message);

            return message.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending a message ]", e);
            throw new JMSException(e.getMessage());
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    private TextMessage createJMSMessage(String text, Session session) throws JMSException {
        TextMessage jmsMessage = session.createTextMessage();
        jmsMessage.setJMSReplyTo(eventBus);
        jmsMessage.setText(text);
        return jmsMessage;
    }

    private javax.jms.MessageProducer getProducer(Session session, Destination destination, long timeout) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(timeout);
        return producer;
    }
}