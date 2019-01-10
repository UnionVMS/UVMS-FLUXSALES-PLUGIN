package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.mapper;

import eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesReportRequest;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.exception.MappingException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class PostMsgTypeMapperTest {

    @InjectMocks
    private PostMsgTypeMapper postMsgTypeMapper;

    @Mock
    private StartupBean startupBean;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private String fluxNodes = "GBR,IRL:AGRI";

    @Test
    public void testWrapInPostMsgTypeWhenADIsNull() throws Exception {
        final String AD = null;
        final String DF = "df";

        SendSalesReportRequest toBeWrapped = new SendSalesReportRequest();

        exception.expect(NullPointerException.class);
        exception.expectMessage("AD can't be null");
        postMsgTypeMapper.wrapInPostMsgType(toBeWrapped, DF, AD);
        verifyNoMoreInteractions(startupBean);
    }

    @Test
    public void testWrapInPostMsgTypeWhenDFIsNull() throws Exception {
        final String AD = "ad";
        final String DF = null;

        SendSalesReportRequest toBeWrapped = new SendSalesReportRequest();

        exception.expect(NullPointerException.class);
        exception.expectMessage("DF can't be null");
        postMsgTypeMapper.wrapInPostMsgType(toBeWrapped, DF, AD);
        verifyNoMoreInteractions(startupBean);
    }

    @Test
    public void testWrapInPostMsgTypeWhenToBeWrappedIsNull() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("Object to be wrapped can't be null");
        postMsgTypeMapper.wrapInPostMsgType(null, "null", "null");
        verifyNoMoreInteractions(startupBean);
    }

    @Test
    public void testWrapInPostMsgTypeWhenObjectIsNotKnownToJAXB() throws Exception {
        Object toBeWrapped = new Object();

        doReturn(fluxNodes).when(startupBean).getSetting("flux_nodes");
        exception.expect(MappingException.class);
        exception.expectMessage("Could not wrap object " + toBeWrapped.toString() + " in post msg");
        postMsgTypeMapper.wrapInPostMsgType(toBeWrapped, "df", "ad");
        verifyNoMoreInteractions(startupBean);
    }

}