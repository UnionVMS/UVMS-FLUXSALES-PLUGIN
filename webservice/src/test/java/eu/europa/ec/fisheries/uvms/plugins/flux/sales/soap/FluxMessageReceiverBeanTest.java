package eu.europa.ec.fisheries.uvms.plugins.flux.sales.soap;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesQueryMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.constants.FluxDataFlowName;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper.FLUXSalesQueryMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper.FLUXSalesReportMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper.FLUXSalesResponseMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.ValidationService;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.XsdValidatorService;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.helper.Connector2BridgeRequestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Element;
import org.xmlunit.validation.ValidationProblem;
import org.xmlunit.validation.ValidationResult;
import xeu.bridge_connector.v1.Connector2BridgeRequest;
import xeu.bridge_connector.v1.Connector2BridgeResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FluxMessageReceiverBeanTest {

    @InjectMocks
    private FluxMessageReceiverBean fluxMessageReceiverBean;

    @Mock
    private FLUXSalesReportMessageMapper fluxSalesReportMessageMapper;

    @Mock
    private FLUXSalesQueryMessageMapper fluxSalesQueryMessageMapper;

    @Mock
    private StartupBean startupBean;

    @Mock
    private ExchangeService exchangeService;

    @Mock
    private Connector2BridgeRequestHelper requestHelper;

    @Mock
    private FLUXSalesResponseMessageMapper fluxSalesResponseMessageMapper;

    @Mock
    private XsdValidatorService xsdValidatorService;

    private final String fr = "BEL";

    private final String on = "abcdefg";


    @Test
    public void postSalesReportWhenSuccess() throws Exception {
        //data set
        Connector2BridgeRequest request = new Connector2BridgeRequest();
        Report report = new Report();

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn("FLUXSalesReportMessage").when(requestHelper).determineMessageType(request);
        doReturn(report).when(fluxSalesReportMessageMapper).mapToReport(request);
        doReturn(fr).when(requestHelper).getFRPropertyOrNull(request);
        doReturn(on).when(requestHelper).getONPropertyOrNull(request);
        doReturn(new ValidationResult(true, Lists.<ValidationProblem>newArrayList())).when(xsdValidatorService).doesMessagePassXsdValidation(any(Element.class));


        //execute
        Connector2BridgeResponse response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(requestHelper).determineMessageType(request);
        verify(fluxSalesReportMessageMapper).mapToReport(request);
        verify(requestHelper).getFRPropertyOrNull(request);
        verify(requestHelper).getONPropertyOrNull(request);
        verify(exchangeService).sendSalesReportToExchange(report, fr, on);
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService, requestHelper);

        assertEquals("OK", response.getStatus());
    }

    @Test
    public void postSalesReportWhenProcessingMessageGoesWrong() throws Exception {
        //data set
        Connector2BridgeRequest request = new Connector2BridgeRequest();
        request.setDF(FluxDataFlowName.SALES_REPORT);
        Report report = new Report();

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn("FLUXSalesReportMessage").when(requestHelper).determineMessageType(request);
        doReturn(report).when(fluxSalesReportMessageMapper).mapToReport(request);
        doReturn(fr).when(requestHelper).getFRPropertyOrNull(request);
        doReturn(on).when(requestHelper).getONPropertyOrNull(request);
        doThrow(new RuntimeException("oops")).when(exchangeService).sendSalesReportToExchange(report, fr, on);
        doReturn(new ValidationResult(true, Lists.<ValidationProblem>newArrayList())).when(xsdValidatorService).doesMessagePassXsdValidation(any(Element.class));

        //execute
        Connector2BridgeResponse response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(requestHelper).determineMessageType(request);
        verify(fluxSalesReportMessageMapper).mapToReport(request);
        verify(requestHelper).getFRPropertyOrNull(request);
        verify(requestHelper).getONPropertyOrNull(request);
        verify(exchangeService).sendSalesReportToExchange(report, fr, on);
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService, requestHelper);

        assertEquals("NOK", response.getStatus());
    }

    @Test
    public void postSalesQueryWhenProcessingMessageGoesWrong() throws Exception {
        //data set
        Connector2BridgeRequest request = new Connector2BridgeRequest();
        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage();

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn("FLUXSalesQueryMessage").when(requestHelper).determineMessageType(request);
        doReturn(query).when(fluxSalesQueryMessageMapper).mapToSalesQuery(request);
        doReturn(fr).when(requestHelper).getFRPropertyOrNull(request);
        doReturn(on).when(requestHelper).getONPropertyOrNull(request);
        doThrow(new RuntimeException("oops")).when(exchangeService).sendSalesQueryToExchange(query, fr, on);
        doReturn(new ValidationResult(true, Lists.<ValidationProblem>newArrayList())).when(xsdValidatorService).doesMessagePassXsdValidation(any(Element.class));

        //execute
        Connector2BridgeResponse response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(requestHelper).determineMessageType(request);
        verify(fluxSalesQueryMessageMapper).mapToSalesQuery(request);
        verify(requestHelper).getFRPropertyOrNull(request);
        verify(requestHelper).getONPropertyOrNull(request);
        verify(exchangeService).sendSalesQueryToExchange(query, fr, on);
        verifyNoMoreInteractions(fluxSalesQueryMessageMapper, startupBean, exchangeService, requestHelper);

        assertEquals("NOK", response.getStatus());
    }

    @Test
    public void postSalesQueryWhenSuccess() throws Exception {
        //data set
        Connector2BridgeRequest request = new Connector2BridgeRequest();
        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage();

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn("FLUXSalesQueryMessage").when(requestHelper).determineMessageType(request);
        doReturn(query).when(fluxSalesQueryMessageMapper).mapToSalesQuery(request);
        doReturn(fr).when(requestHelper).getFRPropertyOrNull(request);
        doReturn(on).when(requestHelper).getONPropertyOrNull(request);
        doReturn(new ValidationResult(true, Lists.<ValidationProblem>newArrayList())).when(xsdValidatorService).doesMessagePassXsdValidation(any(Element.class));

        //execute
        Connector2BridgeResponse response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(requestHelper).determineMessageType(request);
        verify(fluxSalesQueryMessageMapper).mapToSalesQuery(request);
        verify(requestHelper).getFRPropertyOrNull(request);
        verify(requestHelper).getONPropertyOrNull(request);
        verify(exchangeService).sendSalesQueryToExchange(query, fr, on);
        verifyNoMoreInteractions(fluxSalesQueryMessageMapper, startupBean, exchangeService, requestHelper);

        assertEquals("OK", response.getStatus());
    }

    @Test
    public void postSalesResponseWhenProcessingMessageGoesWrong() throws Exception {
        //data set
        Connector2BridgeRequest request = new Connector2BridgeRequest();
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage();

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn("FLUXSalesResponseMessage").when(requestHelper).determineMessageType(request);
        doReturn(response).when(fluxSalesResponseMessageMapper).mapToSalesResponse(request);
        doReturn(fr).when(requestHelper).getFRPropertyOrNull(request);
        doReturn(on).when(requestHelper).getONPropertyOrNull(request);
        doThrow(new RuntimeException("oops")).when(exchangeService).sendSalesResponseToExchange(response, fr, on);
        doReturn(new ValidationResult(true, Lists.<ValidationProblem>newArrayList())).when(xsdValidatorService).doesMessagePassXsdValidation(any(Element.class));

        //execute
        Connector2BridgeResponse Connector2BridgeResponse = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(requestHelper).determineMessageType(request);
        verify(fluxSalesResponseMessageMapper).mapToSalesResponse(request);
        verify(requestHelper).getFRPropertyOrNull(request);
        verify(requestHelper).getONPropertyOrNull(request);
        verify(exchangeService).sendSalesResponseToExchange(response, fr, on);
        verifyNoMoreInteractions(fluxSalesResponseMessageMapper, startupBean, exchangeService, requestHelper);

        assertEquals("NOK", Connector2BridgeResponse.getStatus());
    }

    @Test
    public void postSalesResponseWhenSuccess() throws Exception {
        //data set
        Connector2BridgeRequest request = new Connector2BridgeRequest();
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage();

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn("FLUXSalesResponseMessage").when(requestHelper).determineMessageType(request);
        doReturn(response).when(fluxSalesResponseMessageMapper).mapToSalesResponse(request);
        doReturn(fr).when(requestHelper).getFRPropertyOrNull(request);
        doReturn(on).when(requestHelper).getONPropertyOrNull(request);
        doReturn(new ValidationResult(true, Lists.<ValidationProblem>newArrayList())).when(xsdValidatorService).doesMessagePassXsdValidation(any(Element.class));

        //execute
        Connector2BridgeResponse Connector2BridgeResponse = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(requestHelper).determineMessageType(request);
        verify(fluxSalesResponseMessageMapper).mapToSalesResponse(request);
        verify(requestHelper).getFRPropertyOrNull(request);
        verify(requestHelper).getONPropertyOrNull(request);
        verify(exchangeService).sendSalesResponseToExchange(response, fr, on);
        verifyNoMoreInteractions(fluxSalesResponseMessageMapper, startupBean, exchangeService, requestHelper);

        assertEquals("OK", Connector2BridgeResponse.getStatus());
    }

    @Test
    public void postWhenTheStartupBeanHasNotBeenEnabled() throws Exception {
        //data set
        Connector2BridgeRequest request = new Connector2BridgeRequest();
        request.setDF(FluxDataFlowName.SALES_REPORT);

        //mock
        doReturn(false).when(startupBean).isEnabled();

        //execute
        Connector2BridgeResponse response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService, requestHelper);

        assertEquals("NOK", response.getStatus());
    }

    @Test
    public void postWhenInvalidXMLHasBeenProvided() throws Exception {
        //data set
        Connector2BridgeRequest request = new Connector2BridgeRequest();
        request.setDF("Something stupid like I love you");

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn("NonExisting").when(requestHelper).determineMessageType(request);
        doReturn(new ValidationResult(true, Lists.<ValidationProblem>newArrayList())).when(xsdValidatorService).doesMessagePassXsdValidation(any(Element.class));

        //execute
        Connector2BridgeResponse response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(requestHelper).determineMessageType(request);
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService, requestHelper);

        assertEquals("NOK", response.getStatus());
    }

}