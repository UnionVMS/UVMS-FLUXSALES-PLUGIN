package eu.europa.ec.fisheries.uvms.plugins.flux.sales.soap;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesQueryMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.constants.FluxDataFlowName;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper.FLUXSalesQueryMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper.FLUXSalesReportMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper.FLUXSalesResponseMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.RequestTypeHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import xeu.bridge_connector.v1.RequestType;
import xeu.bridge_connector.v1.ResponseType;

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
    private RequestTypeHelper requestTypeHelper;

    @Mock
    private FLUXSalesResponseMessageMapper fluxSalesResponseMessageMapper;

    @Test
    public void postSalesReportWhenSuccess() throws Exception {
        //data set
        RequestType request = new RequestType();

        Report report = new Report();

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn(report).when(fluxSalesReportMessageMapper).mapToReport(request);
        doReturn("FLUXSalesReportMessage").when(requestTypeHelper).determineMessageType(request);

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(fluxSalesReportMessageMapper).mapToReport(request);
        verify(exchangeService).sendSalesReportToExchange(report);
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService);

        assertEquals("OK", response.getStatus());
    }

    @Test
    public void postSalesReportWhenProcessingMessageGoesWrong() throws Exception {
        //data set
        RequestType request = new RequestType();
        request.setDF(FluxDataFlowName.SALES_REPORT);

        Report report = new Report();

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn(report).when(fluxSalesReportMessageMapper).mapToReport(request);
        doReturn("FLUXSalesReportMessage").when(requestTypeHelper).determineMessageType(request);
        doThrow(new RuntimeException("oops")).when(exchangeService).sendSalesReportToExchange(report);

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(fluxSalesReportMessageMapper).mapToReport(request);
        verify(exchangeService).sendSalesReportToExchange(report);
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService);

        assertEquals("NOK", response.getStatus());
    }

    @Test
    public void postSalesQueryWhenSuccess() throws Exception {
        //data set
        RequestType request = new RequestType();

        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage();

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn(query).when(fluxSalesQueryMessageMapper).mapToSalesQuery(request);
        doReturn("FLUXSalesQueryMessage").when(requestTypeHelper).determineMessageType(request);
        doThrow(new RuntimeException("oops")).when(exchangeService).sendSalesQueryToExchange(query);

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(fluxSalesQueryMessageMapper).mapToSalesQuery(request);
        verify(exchangeService).sendSalesQueryToExchange(query);
        verifyNoMoreInteractions(fluxSalesQueryMessageMapper, startupBean, exchangeService);

        assertEquals("NOK", response.getStatus());
    }

    @Test
    public void postSalesQueryWhenProcessingMessageGoesWrong() throws Exception {
        //data set
        RequestType request = new RequestType();

        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage();

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn(query).when(fluxSalesQueryMessageMapper).mapToSalesQuery(request);
        doReturn("FLUXSalesQueryMessage").when(requestTypeHelper).determineMessageType(request);

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(fluxSalesQueryMessageMapper).mapToSalesQuery(request);
        verify(exchangeService).sendSalesQueryToExchange(query);
        verifyNoMoreInteractions(fluxSalesQueryMessageMapper, startupBean, exchangeService);

        assertEquals("OK", response.getStatus());
    }

    @Test
    public void postSalesResponseWhenSuccess() throws Exception {
        //data set
        RequestType request = new RequestType();

        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage();

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn(response).when(fluxSalesResponseMessageMapper).mapToSalesResponse(request);
        doReturn("FLUXSalesResponseMessage").when(requestTypeHelper).determineMessageType(request);
        doThrow(new RuntimeException("oops")).when(exchangeService).sendSalesResponseToExchange(response);

        //execute
        ResponseType responseType = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(fluxSalesResponseMessageMapper).mapToSalesResponse(request);
        verify(exchangeService).sendSalesResponseToExchange(response);
        verifyNoMoreInteractions(fluxSalesResponseMessageMapper, startupBean, exchangeService);

        assertEquals("NOK", responseType.getStatus());
    }

    @Test
    public void postSalesResponseWhenProcessingMessageGoesWrong() throws Exception {
        //data set
        RequestType request = new RequestType();

        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage();

        //mock
        doReturn(true).when(startupBean).isEnabled();
        doReturn(response).when(fluxSalesResponseMessageMapper).mapToSalesResponse(request);
        doReturn("FLUXSalesResponseMessage").when(requestTypeHelper).determineMessageType(request);

        //execute
        ResponseType responseType = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verify(fluxSalesResponseMessageMapper).mapToSalesResponse(request);
        verify(exchangeService).sendSalesResponseToExchange(response);
        verifyNoMoreInteractions(fluxSalesResponseMessageMapper, startupBean, exchangeService);

        assertEquals("OK", responseType.getStatus());
    }

    @Test
    public void postWhenTheStartupBeanHasNotBeenEnabled() throws Exception {
        //data set
        RequestType request = new RequestType();
        request.setDF(FluxDataFlowName.SALES_REPORT);

        //mock
        doReturn(false).when(startupBean).isEnabled();

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService);

        assertEquals("NOK", response.getStatus());
    }

    @Test
    public void postWhenInvalidDFHasBeenProvided() throws Exception {
        //data set
        RequestType request = new RequestType();
        request.setDF("Something stupid like I love you");

        //mock
        doReturn(true).when(startupBean).isEnabled();

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isEnabled();
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService);

        assertEquals("NOK", response.getStatus());
    }


}