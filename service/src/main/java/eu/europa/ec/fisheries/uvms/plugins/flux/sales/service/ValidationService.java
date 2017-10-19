package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service;

import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.producer.PluginMessageProducer;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.helper.Connector2BridgeRequestHelper;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.SalesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.ValidationQualityAnalysisMapper;
import lombok.extern.slf4j.Slf4j;
import org.xmlunit.validation.ValidationProblem;
import xeu.bridge_connector.v1.Connector2BridgeRequest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.Arrays;

@Stateless
@Slf4j
public class ValidationService {

    @EJB
    private Connector2BridgeRequestHelper requestHelper;

    @EJB
    private PluginMessageProducer producer;

    public void sendMessageToSales(Connector2BridgeRequest request, Iterable<ValidationProblem> problems) {
        try {
            StringBuilder builder = new StringBuilder();
            for (ValidationProblem problem : problems) {
                builder.append(problem.getMessage() + "\n");
            }

            ValidationQualityAnalysisType validationQualityAnalysis = ValidationQualityAnalysisMapper.map("SALE-L00-00-0000", "L00", "ERR", builder.toString(), new ArrayList<String>());

            String requestForSales = SalesModuleRequestMapper.createRespondToInvalidMessageRequest(requestHelper.getONPropertyOrNull(request),
                    Arrays.asList(validationQualityAnalysis), "FLUX", requestHelper.getFRPropertyOrNull(request), "FLUXTL_ON");
            String messageForExchange = ExchangeModuleRequestMapper.createReceiveInvalidSalesMessage(requestForSales, "FLUX");

            producer.sendModuleMessage(messageForExchange, ModuleQueue.EXCHANGE);
        } catch (SalesMarshallException e) {
            log.error("Failed to marshall Sales Response", e);
        } catch (JMSException e) {
            log.error("Failed to send createRespondToInvalidMessageRequest to Sales", e);
        } catch (ExchangeModelMarshallException e) {
            log.error("Failed to marshall Exchange request", e);
        }
    }
}
