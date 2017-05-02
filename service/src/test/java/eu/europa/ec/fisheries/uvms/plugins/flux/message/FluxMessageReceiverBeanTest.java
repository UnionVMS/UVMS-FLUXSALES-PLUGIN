package eu.europa.ec.fisheries.uvms.plugins.flux.message;

import eu.europa.ec.fisheries.uvms.plugins.flux.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.constants.FluxDataFlowName;
import eu.europa.ec.fisheries.uvms.plugins.flux.mapper.FLUXSalesQueryMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.mapper.FLUXSalesReportMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.service.ExchangeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import xeu.bridge_connector.v1.RequestType;
import xeu.bridge_connector.v1.ResponseType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)//TODO STIJN
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

    @Test
    public void postSalesReportWhenSuccess() throws Exception {
        //data set
        RequestType request = new RequestType();
        request.setDF(FluxDataFlowName.SALES_REPORT);

        String reportAsString = "report";

        //mock
        doReturn(true).when(startupBean).isIsEnabled();
        doReturn(reportAsString).when(fluxSalesReportMessageMapper).mapToSalesReportString(request);

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isIsEnabled();
        verify(fluxSalesReportMessageMapper).mapToSalesReportString(request);
        verify(exchangeService).sendSalesReportToExchange(reportAsString);
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService);

        assertEquals("OK", response.getStatus());
    }

    @Test
    public void postSalesReportWhenProcessingMessageGoesWrong() throws Exception {
        //data set
        RequestType request = new RequestType();
        request.setDF(FluxDataFlowName.SALES_REPORT);

        String reportAsString = "report";

        //mock
        doReturn(true).when(startupBean).isIsEnabled();
        doReturn(reportAsString).when(fluxSalesReportMessageMapper).mapToSalesReportString(request);
        doThrow(new RuntimeException("oops")).when(exchangeService).sendSalesReportToExchange(reportAsString);

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isIsEnabled();
        verify(fluxSalesReportMessageMapper).mapToSalesReportString(request);
        verify(exchangeService).sendSalesReportToExchange(reportAsString);
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService);

        assertEquals("NOK", response.getStatus());
    }

    @Test
    public void postSalesQueryWhenSuccess() throws Exception {
        //data set
        RequestType request = new RequestType();
        request.setDF(FluxDataFlowName.SALES_QUERY);

        String queryAsString = "query";

        //mock
        doReturn(true).when(startupBean).isIsEnabled();
        doReturn(queryAsString).when(fluxSalesQueryMessageMapper).mapToSalesQueryString(request);
        doThrow(new RuntimeException("oops")).when(exchangeService).sendSalesQueryToExchange(queryAsString);

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isIsEnabled();
        verify(fluxSalesQueryMessageMapper).mapToSalesQueryString(request);
        verify(exchangeService).sendSalesQueryToExchange(queryAsString);
        verifyNoMoreInteractions(fluxSalesQueryMessageMapper, startupBean, exchangeService);

        assertEquals("NOK", response.getStatus());
    }

    @Test
    public void postSalesQueryWhenProcessingMessageGoesWrong() throws Exception {
        //data set
        RequestType request = new RequestType();
        request.setDF(FluxDataFlowName.SALES_QUERY);

        String queryAsString = "query";

        //mock
        doReturn(true).when(startupBean).isIsEnabled();
        doReturn(queryAsString).when(fluxSalesQueryMessageMapper).mapToSalesQueryString(request);

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isIsEnabled();
        verify(fluxSalesQueryMessageMapper).mapToSalesQueryString(request);
        verify(exchangeService).sendSalesQueryToExchange(queryAsString);
        verifyNoMoreInteractions(fluxSalesQueryMessageMapper, startupBean, exchangeService);

        assertEquals("OK", response.getStatus());
    }

    @Test
    public void postWhenTheStartupBeanHasNotBeenEnabled() throws Exception {
        //data set
        RequestType request = new RequestType();
        request.setDF(FluxDataFlowName.SALES_REPORT);

        //mock
        doReturn(false).when(startupBean).isIsEnabled();

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isIsEnabled();
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService);

        assertEquals("NOK", response.getStatus());
    }

    @Test
    public void postWhenInvalidDFHasBeenProvided() throws Exception {
        //data set
        RequestType request = new RequestType();
        request.setDF("Something stupid like I love you");

        //mock
        doReturn(true).when(startupBean).isIsEnabled();

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isIsEnabled();
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService);

        assertEquals("NOK", response.getStatus());
    }


}