package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.helper;

import eu.europa.ec.fisheries.schema.sales.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

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

    @Test
    public void getCountryOfSenderOrNullWhenChainIsComplete() throws Exception {
        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage()
                        .withSalesQuery(new SalesQueryType()
                            .withSubmitterFLUXParty(new FLUXPartyType()
                                    .withIDS(new IDType()
                                            .withValue("1"))));
        assertEquals("1", queryHelper.getCountryOfSenderOrNull(query));
    }

    @Test
    public void getCountryOfSenderOrNullWhenFluxSalesQueryMessageIsNull() throws Exception {
        assertNull(queryHelper.getCountryOfSenderOrNull(null));
    }


    @Test
    public void getCountryOfSenderOrNullWhenSalesQueryIsNull() throws Exception {
        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage();
        assertNull(queryHelper.getCountryOfSenderOrNull(query));
    }

    @Test
    public void getCountryOfSenderOrNullWhenSubmitterFLUXPartyIsNull() throws Exception {
        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage()
                .withSalesQuery(new SalesQueryType());
        assertNull(queryHelper.getCountryOfSenderOrNull(query));
    }

    @Test
    public void getCountryOfSenderOrNullWhenIDSIsNull() throws Exception {
        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage()
                .withSalesQuery(new SalesQueryType()
                        .withSubmitterFLUXParty(new FLUXPartyType()));
        assertNull(queryHelper.getCountryOfSenderOrNull(query));
    }

    @Test
    public void getCountryOfSenderOrNullWhenIDSIsEmpty() throws Exception {
        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage()
                .withSalesQuery(new SalesQueryType()
                        .withSubmitterFLUXParty(new FLUXPartyType()
                                .withIDS(new ArrayList<IDType>())));
        assertNull(queryHelper.getCountryOfSenderOrNull(query));
    }

    @Test
    public void getCountryOfSenderOrNullWhenValueIsNull() throws Exception {
        FLUXSalesQueryMessage query = new FLUXSalesQueryMessage()
                .withSalesQuery(new SalesQueryType()
                        .withSubmitterFLUXParty(new FLUXPartyType()
                                .withIDS(new IDType())));
        assertNull(queryHelper.getCountryOfSenderOrNull(query));
    }
}