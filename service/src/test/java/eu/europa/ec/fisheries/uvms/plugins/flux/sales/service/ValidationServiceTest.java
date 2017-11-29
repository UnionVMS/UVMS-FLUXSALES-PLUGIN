package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.producer.ExchangeEventMessageProducerBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.helper.Connector2BridgeRequestHelper;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.SalesModuleRequestMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Element;
import org.xmlunit.validation.ValidationProblem;
import xeu.bridge_connector.v1.Connector2BridgeRequest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SalesModuleRequestMapper.class, ExchangeModuleRequestMapper.class})
@PowerMockIgnore({"javax.management.*"})
public class ValidationServiceTest {

    @InjectMocks
    private ValidationService validationService;

    @Mock
    ExchangeEventMessageProducerBean producer;

    @Mock
    private Connector2BridgeRequestHelper helper;

    @Test
    public void sendMessageToSales() throws Exception {
        mockStatic(SalesModuleRequestMapper.class, ExchangeModuleRequestMapper.class);

        List<ValidationProblem> problems = Arrays.asList(new ValidationProblem("problem 1", 1, 1, ValidationProblem.ProblemType.ERROR),
                new ValidationProblem("problem 2", 1, 1, ValidationProblem.ProblemType.ERROR),
                new ValidationProblem("problem 3", 1, 1, ValidationProblem.ProblemType.ERROR),
                new ValidationProblem("problem 4", 1, 1, ValidationProblem.ProblemType.ERROR),
                new ValidationProblem("problem 5", 1, 1, ValidationProblem.ProblemType.ERROR));

        Element elementMock = mock(Element.class);

        Connector2BridgeRequest connector2BridgeRequest = new Connector2BridgeRequest();
        connector2BridgeRequest.setAny(elementMock);

        when(SalesModuleRequestMapper.createRespondToInvalidMessageRequest(eq("ON"), any(List.class),
                eq("FLUX"), eq("FR"), eq("FLUXTL_ON"))).thenReturn("createRespondToInvalidMessageRequest");
        when(ExchangeModuleRequestMapper.createReceiveInvalidSalesMessage(eq("createRespondToInvalidMessageRequest"), eq("ON"), eq("FR"), isA(Date.class), eq("FLUX"), eq(PluginType.FLUX)))
                .thenReturn("createReceiveInvalidSalesMessage");

        doReturn("ON").when(helper).getONPropertyOrNull(any(Connector2BridgeRequest.class));
        doReturn("FR").when(helper).getFRPropertyOrNull(any(Connector2BridgeRequest.class));

        validationService.sendMessageToSales(connector2BridgeRequest, problems);

        verify(producer).sendModuleMessage("createReceiveInvalidSalesMessage", null);

        verifyStatic(ValidationService.class);
        SalesModuleRequestMapper.createRespondToInvalidMessageRequest(eq("ON"), any(List.class),
                eq("FLUX"), eq("FR"), eq("FLUXTL_ON"));
        ExchangeModuleRequestMapper.createReceiveInvalidSalesMessage(eq("createRespondToInvalidMessageRequest"), eq("ON"), eq("FR"), isA(Date.class), eq("FLUX"), eq(PluginType.FLUX));
    }
}