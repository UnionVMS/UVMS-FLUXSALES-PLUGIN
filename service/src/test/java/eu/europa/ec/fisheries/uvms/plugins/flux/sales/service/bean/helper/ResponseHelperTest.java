package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.sales.FLUXResponseDocumentType;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.IDType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ResponseHelperTest {

    private ResponseHelper responseHelper;

    @Before
    public void init() {
        responseHelper = new ResponseHelper();
    }

    @Test
    public void getGuidOrNullWhenChainIsComplete() throws Exception {
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(new FLUXResponseDocumentType()
                                .withIDS(new IDType()
                                        .withValue("1")));
        assertEquals("1", responseHelper.getGuidOrNull(response));
    }

    @Test
    public void getGuidOrNullWhenResponseIsNull() throws Exception {
        assertNull(responseHelper.getGuidOrNull(null));
    }

    @Test
    public void getGuidOrNullWhenFluxResponseDocumentIsNull() throws Exception {
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage();
        assertNull(responseHelper.getGuidOrNull(response));
    }

    @Test
    public void getGuidOrNullWhenIDSIsNull() throws Exception {
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(new FLUXResponseDocumentType());
        assertNull(responseHelper.getGuidOrNull(response));
    }

    @Test
    public void getGuidOrNullWhenIDSIsEmpty() throws Exception {
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(new FLUXResponseDocumentType()
                                .withIDS(new ArrayList<IDType>()));
        assertNull(responseHelper.getGuidOrNull(response));
    }

    @Test
    public void getGuidOrNullWhenValueIsNull() throws Exception {
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(new FLUXResponseDocumentType()
                                .withIDS(new IDType()));
        assertNull(responseHelper.getGuidOrNull(response));
    }

}