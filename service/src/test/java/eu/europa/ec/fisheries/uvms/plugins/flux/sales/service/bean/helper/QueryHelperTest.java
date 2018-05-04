package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesQueryMessage;
import eu.europa.ec.fisheries.schema.sales.IDType;
import eu.europa.ec.fisheries.schema.sales.SalesQueryType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class QueryHelperTest {

    private QueryHelper queryHelper;

    @Before
    public void init() {
        queryHelper = new QueryHelper();
    }

    @Test
    public void getGuidOrNullWhenChainIsComplete() throws Exception {
        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage()
                .withSalesQuery(new SalesQueryType()
                    .withID(new IDType()
                            .withValue("1")));
        assertEquals("1", queryHelper.getGuidOrNull(query));
    }

    @Test
    public void getGuidOrNullWhenQueryIsNull() throws Exception {
        assertNull(queryHelper.getGuidOrNull(null));
    }

    @Test
    public void getGuidOrNullWhenSalesQueryIsNull() throws Exception {
        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage();
        assertNull(queryHelper.getGuidOrNull(query));
    }

    @Test
    public void getGuidOrNullWhenIDIsNull() throws Exception {
        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage()
                .withSalesQuery(new SalesQueryType());
        assertNull(queryHelper.getGuidOrNull(query));
    }

    @Test
    public void getGuidOrNullWhenValueIsNull() throws Exception {
        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage()
                .withSalesQuery(new SalesQueryType()
                                .withID(new IDType()));
        assertNull(queryHelper.getGuidOrNull(query));
    }
}