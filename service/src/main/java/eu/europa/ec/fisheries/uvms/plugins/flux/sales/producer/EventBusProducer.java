package eu.europa.ec.fisheries.uvms.plugins.flux.sales.producer;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;

public interface EventBusProducer {

    String sendEventBusMessage(String text, String serviceName) throws MessageException;

    String getDestinationName();

}
