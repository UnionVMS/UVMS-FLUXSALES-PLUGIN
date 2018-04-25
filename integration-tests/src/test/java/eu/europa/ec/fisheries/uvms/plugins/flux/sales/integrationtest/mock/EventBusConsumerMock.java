package eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.mock;

import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.test.producer.EventBusMessageProducer;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven(mappedName = ExchangeModelConstants.PLUGIN_EVENTBUS, activationConfig = {
        @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = ExchangeModelConstants.SERVICE_NAME + " = '" + ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE + "'"),
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = ExchangeModelConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = ExchangeModelConstants.DESTINATION_TYPE_TOPIC),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = ExchangeModelConstants.EVENTBUS_NAME),
        @ActivationConfigProperty(propertyName = "destinationJndiName", propertyValue = ExchangeModelConstants.PLUGIN_EVENTBUS),
        @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE),
        @ActivationConfigProperty(propertyName = "clientId", propertyValue = ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE),
        @ActivationConfigProperty(propertyName = "connectionFactoryJndiName", propertyValue = ExchangeModelConstants.CONNECTION_FACTORY)
})
public class EventBusConsumerMock implements MessageListener {

    @EJB
    private EventBusMessageProducer eventBusMessageProducer;

    @Override
    public void onMessage(Message message) {
        sendRegisterSuccessful();
        sendStart();
    }

    private void sendStart() {
        eventBusMessageProducer.sendMessageToEventBus(START, "eu.europa.ec.fisheries.uvms.plugins.flux.sales");
    }

    private void sendRegisterSuccessful() {
        eventBusMessageProducer.sendMessageToEventBus(REGISTER_SUCCESSFUL, "eu.europa.ec.fisheries.uvms.plugins.flux.sales.PLUGIN_RESPONSE");
    }

    private static final String REGISTER_SUCCESSFUL = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<ns2:RegisterServiceResponse xmlns:ns2=\"urn:registry.exchange.schema.fisheries.ec.europa.eu:v1\">\n" +
            "    <method>REGISTER_SERVICE</method>\n" +
            "    <ack>\n" +
            "        <messageId>ID:ALV-VPC-1048-20088-1515751346852-131:1:32:1:1</messageId>\n" +
            "        <type>OK</type>\n" +
            "    </ack>\n" +
            "    <service>\n" +
            "        <serviceResponseMessageName>eu.europa.ec.fisheries.uvms.plugins.flux.salesPLUGIN_RESPONSE</serviceResponseMessageName>\n" +
            "        <serviceClassName>eu.europa.ec.fisheries.uvms.plugins.flux.sales</serviceClassName>\n" +
            "        <name>flux-sales-plugin</name>\n" +
            "        <description>Plugin for sending and receiving data to and from FLUX for Sales messages</description>\n" +
            "        <pluginType>FLUX</pluginType>\n" +
            "        <settingList>\n" +
            "            <setting>\n" +
            "                <key>eu.europa.ec.fisheries.uvms.plugins.flux.sales.CLIENT_ID</key>\n" +
            "                <value>flux-sales-plugin</value>\n" +
            "            </setting>\n" +
            "            <setting>\n" +
            "                <key>eu.europa.ec.fisheries.uvms.plugins.flux.sales.DF</key>\n" +
            "                <value>urn:un:unece:uncefact:fisheries:FLUX:SALES:EU:2</value>\n" +
            "            </setting>\n" +
            "            <setting>\n" +
            "                <key>eu.europa.ec.fisheries.uvms.plugins.flux.sales.FLUX_ENDPOINT</key>\n" +
            "                <value>http://localhost:8080/test/FLUXReceiverMock</value>\n" +
            "            </setting>\n" +
            "        </settingList>\n" +
            "        <capabilityList>\n" +
            "            <capability>\n" +
            "                <type>SAMPLING</type>\n" +
            "                <value>TRUE</value>\n" +
            "            </capability>\n" +
            "            <capability>\n" +
            "                <type>MULTIPLE_OCEAN</type>\n" +
            "                <value>TRUE</value>\n" +
            "            </capability>\n" +
            "            <capability>\n" +
            "                <type>POLLABLE</type>\n" +
            "                <value>TRUE</value>\n" +
            "            </capability>\n" +
            "            <capability>\n" +
            "                <type>ONLY_SINGLE_OCEAN</type>\n" +
            "                <value>TRUE</value>\n" +
            "            </capability>\n" +
            "            <capability>\n" +
            "                <type>CONFIGURABLE</type>\n" +
            "                <value>TRUE</value>\n" +
            "            </capability>\n" +
            "        </capabilityList>\n" +
            "        <status>STARTED</status>\n" +
            "        <active>true</active>\n" +
            "    </service>\n" +
            "</ns2:RegisterServiceResponse>\n";

    private static final String START = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<ns2:StartRequest xmlns:ns2=\"urn:plugin.exchange.schema.fisheries.ec.europa.eu:v1\">\n" +
            "    <method>START</method>\n" +
            "</ns2:StartRequest>";
}
