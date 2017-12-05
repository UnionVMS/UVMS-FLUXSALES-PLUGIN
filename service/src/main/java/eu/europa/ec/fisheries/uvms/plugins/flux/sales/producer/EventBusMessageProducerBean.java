package eu.europa.ec.fisheries.uvms.plugins.flux.sales.producer;

import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class EventBusMessageProducerBean extends AbstractEventBusProducer {

    @Override
    public String getDestinationName() { return ExchangeModelConstants.PLUGIN_EVENTBUS; }

}
