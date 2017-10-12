package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service;

import eu.europa.ec.fisheries.schema.rules.rule.v1.ErrorType;
import eu.europa.ec.fisheries.schema.rules.rule.v1.ValidationMessageType;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.producer.PluginMessageProducer;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.helper.Connector2BridgeRequestHelper;
import eu.europa.ec.fisheries.uvms.rules.model.dto.ValidationResultDto;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.SalesModuleRequestMapper;
import lombok.extern.slf4j.Slf4j;
import org.xmlunit.validation.ValidationProblem;
import org.xmlunit.validation.ValidationResult;
import xeu.bridge_connector.v1.Connector2BridgeRequest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import java.util.Arrays;

@Stateless
@Slf4j
public class ValidationService {

    @EJB
    private XsdValidatorService xsdValidatorService;

    @EJB
    private Connector2BridgeRequestHelper requestHelper;

    @EJB
    private PluginMessageProducer producer;

    public boolean validate(Connector2BridgeRequest request) {
        ValidationResult validationResult = xsdValidatorService.doesMessagePassXsdValidation(request.getAny());

        if (!validationResult.isValid()) {
            sendMessageToSales(request, validationResult.getProblems());
        }

        return validationResult.isValid();
    }

    private void sendMessageToSales(Connector2BridgeRequest request, Iterable<ValidationProblem> problems) {
        try {
            StringBuilder builder = new StringBuilder();
            for (ValidationProblem problem : problems) {
                builder.append(problem.getMessage());
            }

            ValidationResultDto validationResultDto = new ValidationResultDto();
            ValidationMessageType validationMessageType = new ValidationMessageType();
            validationMessageType.setBrId("SALE-L00-00-0000");
            validationMessageType.setLevel("L00");
            validationMessageType.setErrorType(ErrorType.ERROR);
            validationMessageType.setMessage(builder.toString());

            validationResultDto.setValidationMessages(Arrays.asList(validationMessageType));
            validationResultDto.setIsError(true);

            String requestForSales = SalesModuleRequestMapper.createRespondToInvalidMessageRequest(requestHelper.getONPropertyOrNull(request),
                    validationResultDto, "FLUX", requestHelper.getFRPropertyOrNull(request), "FLUXTL_ON");

            producer.sendModuleMessage(requestForSales, ModuleQueue.SALES);
        } catch (SalesMarshallException e) {
            log.error("Failed to marshall Sales Response", e);
        } catch (JMSException e) {
            log.error("Failed to send createRespondToInvalidMessageRequest to Sales", e);
        }
    }
}
