package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.helper;

import eu.europa.ec.fisheries.schema.sales.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReportHelperTest {

    private ReportHelper reportHelper;

    @Before
    public void init() {
        reportHelper = new ReportHelper();
    }

    @Test
    public void getGuidOrNullWhenChainIsComplete() throws Exception {
        Report report = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                    .withFLUXReportDocument(new FLUXReportDocumentType()
                        .withIDS(new IDType()
                                .withValue("1"))));
        assertEquals("1", reportHelper.getGuidOrNull(report));
    }

    @Test
    public void getGuidOrNullWhenReportIsNull() throws Exception {
        assertNull(reportHelper.getGuidOrNull(null));
    }

    @Test
    public void getGuidOrNullWhenFluxSalesReportMessageIsNull() throws Exception {
        Report report = new Report();
        assertNull(reportHelper.getGuidOrNull(report));
    }

    @Test
    public void getGuidOrNullWhenFluxReportDocumentIsNull() throws Exception {
        Report report = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage());
        assertNull(reportHelper.getGuidOrNull(report));
    }

    @Test
    public void getGuidOrNullWhenIDSIsNull() throws Exception {
        Report report = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                        .withFLUXReportDocument(new FLUXReportDocumentType()));
        assertNull(reportHelper.getGuidOrNull(report));
    }

    @Test
    public void getGuidOrNullWhenIDSIsEmpty() throws Exception {
        Report report = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                        .withFLUXReportDocument(new FLUXReportDocumentType()
                                .withIDS(new ArrayList<IDType>())));
        assertNull(reportHelper.getGuidOrNull(report));
    }

    @Test
    public void getGuidOrNullWhenValueIsNull() throws Exception {
        Report report = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                        .withFLUXReportDocument(new FLUXReportDocumentType()
                                .withIDS(new IDType())));
        assertNull(reportHelper.getGuidOrNull(report));
    }

    @Test
    public void getCountryOfSenderOrNullWhenChainIsComplete() throws Exception {
        Report report = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                        .withFLUXReportDocument(new FLUXReportDocumentType()
                                .withOwnerFLUXParty(new FLUXPartyType()
                                    .withIDS(new IDType()
                                        .withValue("1")))));
        assertEquals("1", reportHelper.getCountryOfSenderOrNull(report));
    }

    @Test
    public void getCountryOfSenderOrNullWhenReportIsNull() throws Exception {
        assertNull(reportHelper.getCountryOfSenderOrNull(null));
    }

    @Test
    public void getCountryOfSenderOrNullWhenFluxSalesReportMessageIsNull() throws Exception {
        Report report = new Report();
        assertNull(reportHelper.getCountryOfSenderOrNull(report));
    }

    @Test
    public void getCountryOfSenderOrNullWhenFluxReportDocumentIsNull() throws Exception {
        Report report = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage());
        assertNull(reportHelper.getCountryOfSenderOrNull(report));
    }

    @Test
    public void getCountryOfSenderOrNullWhenOwnerFLUXPartyIsNull() throws Exception {
        Report report = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                        .withFLUXReportDocument(new FLUXReportDocumentType()));
        assertNull(reportHelper.getCountryOfSenderOrNull(report));
    }

    @Test
    public void getCountryOfSenderOrNullWhenIDSIsNull() throws Exception {
        Report report = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                        .withFLUXReportDocument(new FLUXReportDocumentType()
                                .withOwnerFLUXParty(new FLUXPartyType())));
        assertNull(reportHelper.getCountryOfSenderOrNull(report));
    }

    @Test
    public void getCountryOfSenderOrNullWhenIDSIsEmpty() throws Exception {
        Report report = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                        .withFLUXReportDocument(new FLUXReportDocumentType()
                                .withOwnerFLUXParty(new FLUXPartyType())
                                .withIDS(new ArrayList<IDType>())));
        assertNull(reportHelper.getCountryOfSenderOrNull(report));
    }

    @Test
    public void getCountryOfSenderOrNullWhenValueIsNull() throws Exception {
        Report report = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                        .withFLUXReportDocument(new FLUXReportDocumentType()
                                .withOwnerFLUXParty(new FLUXPartyType())
                                .withIDS(new IDType())));
        assertNull(reportHelper.getCountryOfSenderOrNull(report));
    }

}