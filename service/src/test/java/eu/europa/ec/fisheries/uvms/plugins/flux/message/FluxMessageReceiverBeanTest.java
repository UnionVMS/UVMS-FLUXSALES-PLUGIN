package eu.europa.ec.fisheries.uvms.plugins.flux.message;

import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.plugins.flux.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.constants.FluxDataFlowName;
import eu.europa.ec.fisheries.uvms.plugins.flux.framework.XMLResourceLoader;
import eu.europa.ec.fisheries.uvms.plugins.flux.mapper.FLUXSalesQueryMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.mapper.FLUXSalesReportMessageMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.plugins.flux.service.RequestTypeHelper;
import org.junit.Before;
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

    public static final String TEST_XML_DOCUMENT = "test_xml_document";
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

    private XMLResourceLoader xmlResourceLoader;

    @Before
    public void setUp() throws Exception {
        xmlResourceLoader = new XMLResourceLoader();
    }

    @Test
    public void postSalesReportWhenSuccess() throws Exception {
        //data set
        RequestType request = new RequestType();

        Report report = new Report();

        //mock
        doReturn(true).when(startupBean).isIsEnabled();
        doReturn(report).when(fluxSalesReportMessageMapper).mapToReport(request);
        doReturn("FLUXSalesReportMessage").when(requestTypeHelper).determineMessageType(request);

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isIsEnabled();
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
        doReturn(true).when(startupBean).isIsEnabled();
        doReturn(report).when(fluxSalesReportMessageMapper).mapToReport(request);
        doReturn("FLUXSalesReportMessage").when(requestTypeHelper).determineMessageType(request);
        doThrow(new RuntimeException("oops")).when(exchangeService).sendSalesReportToExchange(report);

        //execute
        ResponseType response = fluxMessageReceiverBean.post(request);

        //verify and assert
        verify(startupBean).isIsEnabled();
        verify(fluxSalesReportMessageMapper).mapToReport(request);
        verify(exchangeService).sendSalesReportToExchange(report);
        verifyNoMoreInteractions(fluxSalesReportMessageMapper, startupBean, exchangeService);

        assertEquals("NOK", response.getStatus());
    }

    @Test
    public void postSalesQueryWhenSuccess() throws Exception {
        //data set
        RequestType request = new RequestType();

        String queryAsString = "query";

        //mock
        doReturn(true).when(startupBean).isIsEnabled();
        doReturn(queryAsString).when(fluxSalesQueryMessageMapper).mapToSalesQueryString(request);
        doReturn("FLUXSalesQueryMessage").when(requestTypeHelper).determineMessageType(request);
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

        String queryAsString = "query";

        //mock
        doReturn(true).when(startupBean).isIsEnabled();
        doReturn(queryAsString).when(fluxSalesQueryMessageMapper).mapToSalesQueryString(request);
        doReturn("FLUXSalesQueryMessage").when(requestTypeHelper).determineMessageType(request);

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