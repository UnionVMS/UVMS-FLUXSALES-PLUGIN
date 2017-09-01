package eu.europa.ec.fisheries.uvms.plugins.flux.sales.mapper;

import eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesReportRequest;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.exception.MappingException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import xeu.connector_bridge.v1.POSTMSG;

import static org.junit.Assert.assertEquals;

public class PostMsgTypeMapperTest {

    private PostMsgTypeMapper postMsgTypeMapper;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        postMsgTypeMapper = new PostMsgTypeMapper();
    }

    @Test
    public void testWrapInPostMsgTypeWhenObjectIsKnownToJAXB() throws Exception {
        final String AD = "ad";
        final String DF = "df";

        SendSalesReportRequest toBeWrapped = new SendSalesReportRequest();

        POSTMSG postMsgType = postMsgTypeMapper.wrapInPostMsgType(toBeWrapped, DF, AD);

        assertEquals(AD, postMsgType.getAD());
        assertEquals(DF, postMsgType.getDF());
    }

    @Test
    public void testWrapInPostMsgTypeWhenADIsNull() throws Exception {
        final String AD = null;
        final String DF = "df";

        SendSalesReportRequest toBeWrapped = new SendSalesReportRequest();

        exception.expect(NullPointerException.class);
        exception.expectMessage("AD can't be null");
        postMsgTypeMapper.wrapInPostMsgType(toBeWrapped, DF, AD);
    }

    @Test
    public void testWrapInPostMsgTypeWhenDFIsNull() throws Exception {
        final String AD = "ad";
        final String DF = null;

        SendSalesReportRequest toBeWrapped = new SendSalesReportRequest();

        exception.expect(NullPointerException.class);
        exception.expectMessage("DF can't be null");
        postMsgTypeMapper.wrapInPostMsgType(toBeWrapped, DF, AD);
    }

    @Test
    public void testWrapInPostMsgTypeWhenToBeWrappedIsNull() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("Object to be wrapped can't be null");
        postMsgTypeMapper.wrapInPostMsgType(null, "null", "null");
    }

    @Test
    public void testWrapInPostMsgTypeWhenObjectIsNotKnownToJAXB() throws Exception {
        Object toBeWrapped = new Object();

        exception.expect(MappingException.class);
        exception.expectMessage("Could not wrap object " + toBeWrapped.toString() + " in post msg");
        postMsgTypeMapper.wrapInPostMsgType(toBeWrapped, "df", "ad");
    }

}