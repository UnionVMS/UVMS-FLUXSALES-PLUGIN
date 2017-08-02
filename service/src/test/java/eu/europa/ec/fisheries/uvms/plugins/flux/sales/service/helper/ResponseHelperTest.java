package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.helper;

import eu.europa.ec.fisheries.schema.sales.*;
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

    @Test
    public void getCountryOfSenderOrNullWhenChainIsComplete() throws Exception {
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage()
                        .withFLUXResponseDocument(new FLUXResponseDocumentType()
                            .withRespondentFLUXParty(new FLUXPartyType()
                                    .withIDS(new IDType()
                                            .withValue("1"))));
        assertEquals("1", responseHelper.getCountryOfSenderOrNull(response));
    }

    @Test
    public void getCountryOfSenderOrNullWhenFluxSalesResponseMessageIsNull() throws Exception {
        assertNull(responseHelper.getCountryOfSenderOrNull(null));
    }


    @Test
    public void getCountryOfSenderOrNullWhenFluxResponseDocumentIsNull() throws Exception {
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage();
        assertNull(responseHelper.getCountryOfSenderOrNull(response));
    }

    @Test
    public void getCountryOfSenderOrNullWhenRespondentFLUXPartyIsNull() throws Exception {
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(new FLUXResponseDocumentType());
        assertNull(responseHelper.getCountryOfSenderOrNull(response));
    }

    @Test
    public void getCountryOfSenderOrNullWhenIDSIsNull() throws Exception {
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(new FLUXResponseDocumentType()
                        .withRespondentFLUXParty(new FLUXPartyType()));
        assertNull(responseHelper.getCountryOfSenderOrNull(response));
    }

    @Test
    public void getCountryOfSenderOrNullWhenIDSIsEmpty() throws Exception {
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(new FLUXResponseDocumentType()
                        .withRespondentFLUXParty(new FLUXPartyType()
                                .withIDS(new ArrayList<IDType>())));
        assertNull(responseHelper.getCountryOfSenderOrNull(response));
    }

    @Test
    public void getCountryOfSenderOrNullWhenValueIsNull() throws Exception {
        FLUXSalesResponseMessage response = new FLUXSalesResponseMessage()
                .withFLUXResponseDocument(new FLUXResponseDocumentType()
                        .withRespondentFLUXParty(new FLUXPartyType()
                                .withIDS(new IDType())));
        assertNull(responseHelper.getCountryOfSenderOrNull(response));
    }
}