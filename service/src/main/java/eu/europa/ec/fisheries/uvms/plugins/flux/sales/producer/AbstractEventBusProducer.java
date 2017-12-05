package eu.europa.ec.fisheries.uvms.plugins.flux.sales.producer;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.*;

public abstract class AbstractEventBusProducer implements EventBusProducer {

    private ConnectionFactory connectionFactory;

    private Destination destination;

    private static final long TIME_TO_LIVE = 60000L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProducer.class);

    @PostConstruct
    public void initializeConnectionFactory() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
        destination = JMSUtils.lookupTopic(getDestinationName());
    }

    protected final ConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            connectionFactory = JMSUtils.lookupConnectionFactory();
        }
        return connectionFactory;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendEventBusMessage(String text, String serviceName) throws MessageException {
        Connection connection = null;
        try {
            connection = getConnectionFactory().createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            TextMessage message = session.createTextMessage();
            message.setText(text);
            message.setStringProperty(ExchangeModelConstants.SERVICE_NAME, serviceName);

            MessageProducer producer = session.createProducer(getDestination());
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(TIME_TO_LIVE);
            producer.send(message);
            LOGGER.info("Message with {} has been successfully sent.", message.getJMSMessageID());
            return message.getJMSMessageID();

        } catch (JMSException e) {
            LOGGER.error("[ Error when sending message. ] {}", e.getMessage());
            throw new MessageException("[ Error when sending message. ]", e);
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    protected final Destination getDestination() {
        if (destination == null) {
            destination = JMSUtils.lookupTopic(getDestinationName());
        }
        return destination;
    }
}
