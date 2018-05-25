package eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.test;

import eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.deployment.TestOnGoodWorkingPlugin;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.mock.PortMock;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.test.producer.EventBusMessageProducer;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.StartupBean;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import xeu.connector_bridge.v1.POSTMSG;

import javax.ejb.EJB;
import javax.xml.ws.handler.MessageContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class SendSalesReportIT extends TestOnGoodWorkingPlugin {

	@EJB
	private StartupBean startupBean;

	@EJB
    private EventBusMessageProducer eventBusMessageProducer;

	@EJB
    private PortMock portMock;

    @Before @After
    public void clear() {
        portMock.clear();
    }

	@Test
	@OperateOnDeployment("good-working-plugin")
	public void pluginPicksUpAMessageFromItsQueueAndSendsIt() throws Exception {
        //wait until plugin has started up
        await().atMost(30, SECONDS)
                .until(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return startupBean.isRegistered();
                    }
                });

        await().atMost(30, SECONDS)
                .until(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return startupBean.isEnabled();
                    }
                });

        //put message that needs to be sent on queue
        eventBusMessageProducer.sendMessageToEventBus(MESSAGE_FROM_EXCHANGE, "eu.europa.ec.fisheries.uvms.plugins.flux.sales");

        //wait until message is "sent"
        await().atMost(30, SECONDS)
                .until(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return !portMock.getAllSentMessages().isEmpty();
                    }
                });

        //assert message content
        assertEquals(1, portMock.getAllSentMessages().size());
        POSTMSG actualMessage = portMock.getAllSentMessages().get(0);
        assertNotNull(actualMessage.getDT());
        assertEquals("IRL", actualMessage.getAD());
        assertEquals("urn:un:unece:uncefact:fisheries:FLUX:SALES:EU:2", actualMessage.getDF());
        assertNotNull(actualMessage.getBUSINESSUUID());
        String actualOutgoingMessage = toString(actualMessage.getAny());
        assertEquals(EXPECTED_OUTGOING_MESSAGE, actualOutgoingMessage);


        //assert headers
        Map<String, Object> requestContext = portMock.getRequestContext();
        Map<String, List<String>> headers = (Map<String, List<String>>) requestContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        assertEquals(Arrays.asList("flux-sales-plugin"), headers.get("connectorID"));
	}

    private String toString(Element node) {
        DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer serializer = lsImpl.createLSSerializer();
        serializer.getDomConfig().setParameter("xml-declaration", false); //by default its true, so set it to false to get String without xml-declaration
        return serializer.writeToString(node);
    }

    private static final String MESSAGE_FROM_EXCHANGE =
            "<ns2:SendSalesReportRequest xmlns:ns2=\"urn:plugin.exchange.schema.fisheries.ec.europa.eu:v1\">\n" +
            "    <method>SEND_SALES_REPORT</method>\n" +
            "    <recipient>IRL</recipient>" +
            "    <report>\n" +
            "        &lt;ns4:Report xmlns=&quot;urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20&quot; xmlns:ns2=&quot;urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20&quot; xmlns:ns4=&quot;eu.europa.ec.fisheries.schema.sales&quot; xmlns:ns3=&quot;eu.europa.ec.fisheries.schema.sales.flux&quot;&gt;\n" +
            "    &lt;ns4:FLUXSalesReportMessage&gt;\n" +
            "        &lt;ns3:FLUXReportDocument&gt;\n" +
            "            &lt;ID schemeID=&quot;UUID&quot;&gt;9786fe08-5476-4cac-b069-90271f8e77b0&lt;/ID&gt;\n" +
            "            &lt;CreationDateTime&gt;\n" +
            "                &lt;ns2:DateTime&gt;2018-05-18T19:30:11.615Z&lt;/ns2:DateTime&gt;\n" +
            "            &lt;/CreationDateTime&gt;\n" +
            "            &lt;PurposeCode listID=&quot;FLUX_GP_PURPOSE&quot;&gt;9&lt;/PurposeCode&gt;\n" +
            "            &lt;Purpose&gt;Sales note example&lt;/Purpose&gt;\n" +
            "            &lt;OwnerFLUXParty&gt;\n" +
            "                &lt;ID schemeID=&quot;FLUX_GP_PARTY&quot;&gt;BEL&lt;/ID&gt;\n" +
            "            &lt;/OwnerFLUXParty&gt;\n" +
            "        &lt;/ns3:FLUXReportDocument&gt;\n" +
            "        &lt;ns3:SalesReport&gt;\n" +
            "            &lt;ItemTypeCode listID=&quot;FLUX_SALES_TYPE&quot;&gt;SN&lt;/ItemTypeCode&gt;\n" +
            "            &lt;IncludedSalesDocument&gt;\n" +
            "                &lt;ID schemeID=&quot;EU_SALES_ID&quot;&gt;BEL-SN-2018-690875&lt;/ID&gt;\n" +
            "                &lt;CurrencyCode listID=&quot;TERRITORY_CURR&quot;&gt;EUR&lt;/CurrencyCode&gt;\n" +
            "                &lt;SpecifiedSalesBatch&gt;\n" +
            "                    &lt;SpecifiedAAPProduct&gt;\n" +
            "                        &lt;SpeciesCode listID=&quot;FAO_SPECIES&quot;&gt;ADI&lt;/SpeciesCode&gt;\n" +
            "                        &lt;WeightMeasure unitCode=&quot;KGM&quot;&gt;5285&lt;/WeightMeasure&gt;\n" +
            "                        &lt;UsageCode listID=&quot;PROD_USAGE&quot;&gt;HCN&lt;/UsageCode&gt;\n" +
            "                        &lt;AppliedAAPProcess&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_FRESHNESS&quot;&gt;B&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_PRESERVATION&quot;&gt;FRE&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_PRESENTATION&quot;&gt;SGT&lt;/TypeCode&gt;\n" +
            "                        &lt;/AppliedAAPProcess&gt;\n" +
            "                        &lt;TotalSalesPrice&gt;\n" +
            "                            &lt;ChargeAmount&gt;1368.57&lt;/ChargeAmount&gt;\n" +
            "                        &lt;/TotalSalesPrice&gt;\n" +
            "                        &lt;SpecifiedSizeDistribution&gt;\n" +
            "                            &lt;CategoryCode listID=&quot;FISH_SIZE_CATEGORY&quot;&gt;7b&lt;/CategoryCode&gt;\n" +
            "                            &lt;ClassCode listID=&quot;FISH_SIZE_CLASS&quot;&gt;LSC&lt;/ClassCode&gt;\n" +
            "                        &lt;/SpecifiedSizeDistribution&gt;\n" +
            "                        &lt;OriginFLUXLocation&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FLUX_LOCATION_TYPE&quot;&gt;AREA&lt;/TypeCode&gt;\n" +
            "                            &lt;ID schemeID=&quot;FAO_AREA&quot;&gt;27.3.d.24&lt;/ID&gt;\n" +
            "                        &lt;/OriginFLUXLocation&gt;\n" +
            "                    &lt;/SpecifiedAAPProduct&gt;\n" +
            "                    &lt;SpecifiedAAPProduct&gt;\n" +
            "                        &lt;SpeciesCode listID=&quot;FAO_SPECIES&quot;&gt;ACG&lt;/SpeciesCode&gt;\n" +
            "                        &lt;WeightMeasure unitCode=&quot;KGM&quot;&gt;7338&lt;/WeightMeasure&gt;\n" +
            "                        &lt;UsageCode listID=&quot;PROD_USAGE&quot;&gt;HCN-INDIRECT&lt;/UsageCode&gt;\n" +
            "                        &lt;AppliedAAPProcess&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_FRESHNESS&quot;&gt;V&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_PRESERVATION&quot;&gt;DRI&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_PRESENTATION&quot;&gt;WNG&lt;/TypeCode&gt;\n" +
            "                        &lt;/AppliedAAPProcess&gt;\n" +
            "                        &lt;TotalSalesPrice&gt;\n" +
            "                            &lt;ChargeAmount&gt;7778.56&lt;/ChargeAmount&gt;\n" +
            "                        &lt;/TotalSalesPrice&gt;\n" +
            "                        &lt;SpecifiedSizeDistribution&gt;\n" +
            "                            &lt;CategoryCode listID=&quot;FISH_SIZE_CATEGORY&quot;&gt;N/A&lt;/CategoryCode&gt;\n" +
            "                            &lt;ClassCode listID=&quot;FISH_SIZE_CLASS&quot;&gt;BMS&lt;/ClassCode&gt;\n" +
            "                        &lt;/SpecifiedSizeDistribution&gt;\n" +
            "                        &lt;OriginFLUXLocation&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FLUX_LOCATION_TYPE&quot;&gt;AREA&lt;/TypeCode&gt;\n" +
            "                            &lt;ID schemeID=&quot;FAO_AREA&quot;&gt;27.3.d.24&lt;/ID&gt;\n" +
            "                        &lt;/OriginFLUXLocation&gt;\n" +
            "                    &lt;/SpecifiedAAPProduct&gt;\n" +
            "                    &lt;SpecifiedAAPProduct&gt;\n" +
            "                        &lt;SpeciesCode listID=&quot;FAO_SPECIES&quot;&gt;AAB&lt;/SpeciesCode&gt;\n" +
            "                        &lt;WeightMeasure unitCode=&quot;KGM&quot;&gt;4876&lt;/WeightMeasure&gt;\n" +
            "                        &lt;UsageCode listID=&quot;PROD_USAGE&quot;&gt;HCN-INDIRECT&lt;/UsageCode&gt;\n" +
            "                        &lt;AppliedAAPProcess&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_FRESHNESS&quot;&gt;B&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_PRESERVATION&quot;&gt;DRI&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_PRESENTATION&quot;&gt;SGT&lt;/TypeCode&gt;\n" +
            "                        &lt;/AppliedAAPProcess&gt;\n" +
            "                        &lt;TotalSalesPrice&gt;\n" +
            "                            &lt;ChargeAmount&gt;1368.57&lt;/ChargeAmount&gt;\n" +
            "                        &lt;/TotalSalesPrice&gt;\n" +
            "                        &lt;SpecifiedSizeDistribution&gt;\n" +
            "                            &lt;CategoryCode listID=&quot;FISH_SIZE_CATEGORY&quot;&gt;N/A&lt;/CategoryCode&gt;\n" +
            "                            &lt;ClassCode listID=&quot;FISH_SIZE_CLASS&quot;&gt;BMS&lt;/ClassCode&gt;\n" +
            "                        &lt;/SpecifiedSizeDistribution&gt;\n" +
            "                        &lt;OriginFLUXLocation&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FLUX_LOCATION_TYPE&quot;&gt;AREA&lt;/TypeCode&gt;\n" +
            "                            &lt;ID schemeID=&quot;FAO_AREA&quot;&gt;27.3.d.24&lt;/ID&gt;\n" +
            "                        &lt;/OriginFLUXLocation&gt;\n" +
            "                    &lt;/SpecifiedAAPProduct&gt;\n" +
            "                    &lt;SpecifiedAAPProduct&gt;\n" +
            "                        &lt;SpeciesCode listID=&quot;FAO_SPECIES&quot;&gt;ADP&lt;/SpeciesCode&gt;\n" +
            "                        &lt;WeightMeasure unitCode=&quot;KGM&quot;&gt;7759&lt;/WeightMeasure&gt;\n" +
            "                        &lt;UsageCode listID=&quot;PROD_USAGE&quot;&gt;IND&lt;/UsageCode&gt;\n" +
            "                        &lt;AppliedAAPProcess&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_FRESHNESS&quot;&gt;V&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_PRESERVATION&quot;&gt;ALI&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_PRESENTATION&quot;&gt;CLA&lt;/TypeCode&gt;\n" +
            "                        &lt;/AppliedAAPProcess&gt;\n" +
            "                        &lt;TotalSalesPrice&gt;\n" +
            "                            &lt;ChargeAmount&gt;2292.13&lt;/ChargeAmount&gt;\n" +
            "                        &lt;/TotalSalesPrice&gt;\n" +
            "                        &lt;SpecifiedSizeDistribution&gt;\n" +
            "                            &lt;CategoryCode listID=&quot;FISH_SIZE_CATEGORY&quot;&gt;N/A&lt;/CategoryCode&gt;\n" +
            "                            &lt;ClassCode listID=&quot;FISH_SIZE_CLASS&quot;&gt;LSC&lt;/ClassCode&gt;\n" +
            "                        &lt;/SpecifiedSizeDistribution&gt;\n" +
            "                        &lt;OriginFLUXLocation&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FLUX_LOCATION_TYPE&quot;&gt;AREA&lt;/TypeCode&gt;\n" +
            "                            &lt;ID schemeID=&quot;FAO_AREA&quot;&gt;27.3.d.24&lt;/ID&gt;\n" +
            "                        &lt;/OriginFLUXLocation&gt;\n" +
            "                    &lt;/SpecifiedAAPProduct&gt;\n" +
            "                    &lt;SpecifiedAAPProduct&gt;\n" +
            "                        &lt;SpeciesCode listID=&quot;FAO_SPECIES&quot;&gt;ABE&lt;/SpeciesCode&gt;\n" +
            "                        &lt;WeightMeasure unitCode=&quot;KGM&quot;&gt;8822&lt;/WeightMeasure&gt;\n" +
            "                        &lt;UsageCode listID=&quot;PROD_USAGE&quot;&gt;HCN-INDIRECT&lt;/UsageCode&gt;\n" +
            "                        &lt;AppliedAAPProcess&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_FRESHNESS&quot;&gt;E&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_PRESERVATION&quot;&gt;BOI&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_PRESENTATION&quot;&gt;LVR-C&lt;/TypeCode&gt;\n" +
            "                        &lt;/AppliedAAPProcess&gt;\n" +
            "                        &lt;TotalSalesPrice&gt;\n" +
            "                            &lt;ChargeAmount&gt;600.83&lt;/ChargeAmount&gt;\n" +
            "                        &lt;/TotalSalesPrice&gt;\n" +
            "                        &lt;SpecifiedSizeDistribution&gt;\n" +
            "                            &lt;CategoryCode listID=&quot;FISH_SIZE_CATEGORY&quot;&gt;N/A&lt;/CategoryCode&gt;\n" +
            "                            &lt;ClassCode listID=&quot;FISH_SIZE_CLASS&quot;&gt;BMS&lt;/ClassCode&gt;\n" +
            "                        &lt;/SpecifiedSizeDistribution&gt;\n" +
            "                        &lt;OriginFLUXLocation&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FLUX_LOCATION_TYPE&quot;&gt;AREA&lt;/TypeCode&gt;\n" +
            "                            &lt;ID schemeID=&quot;FAO_AREA&quot;&gt;27.3.d.24&lt;/ID&gt;\n" +
            "                        &lt;/OriginFLUXLocation&gt;\n" +
            "                    &lt;/SpecifiedAAPProduct&gt;\n" +
            "                    &lt;SpecifiedAAPProduct&gt;\n" +
            "                        &lt;SpeciesCode listID=&quot;FAO_SPECIES&quot;&gt;ACP&lt;/SpeciesCode&gt;\n" +
            "                        &lt;WeightMeasure unitCode=&quot;KGM&quot;&gt;8079&lt;/WeightMeasure&gt;\n" +
            "                        &lt;UsageCode listID=&quot;PROD_USAGE&quot;&gt;HCN-INDIRECT&lt;/UsageCode&gt;\n" +
            "                        &lt;AppliedAAPProcess&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_FRESHNESS&quot;&gt;A&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_PRESERVATION&quot;&gt;DRI&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FISH_PRESENTATION&quot;&gt;HEA&lt;/TypeCode&gt;\n" +
            "                        &lt;/AppliedAAPProcess&gt;\n" +
            "                        &lt;TotalSalesPrice&gt;\n" +
            "                            &lt;ChargeAmount&gt;3626.72&lt;/ChargeAmount&gt;\n" +
            "                        &lt;/TotalSalesPrice&gt;\n" +
            "                        &lt;SpecifiedSizeDistribution&gt;\n" +
            "                            &lt;CategoryCode listID=&quot;FISH_SIZE_CATEGORY&quot;&gt;N/A&lt;/CategoryCode&gt;\n" +
            "                            &lt;ClassCode listID=&quot;FISH_SIZE_CLASS&quot;&gt;BMS&lt;/ClassCode&gt;\n" +
            "                        &lt;/SpecifiedSizeDistribution&gt;\n" +
            "                        &lt;OriginFLUXLocation&gt;\n" +
            "                            &lt;TypeCode listID=&quot;FLUX_LOCATION_TYPE&quot;&gt;AREA&lt;/TypeCode&gt;\n" +
            "                            &lt;ID schemeID=&quot;FAO_AREA&quot;&gt;27.3.d.24&lt;/ID&gt;\n" +
            "                        &lt;/OriginFLUXLocation&gt;\n" +
            "                    &lt;/SpecifiedAAPProduct&gt;\n" +
            "                &lt;/SpecifiedSalesBatch&gt;\n" +
            "                &lt;SpecifiedSalesEvent&gt;\n" +
            "                    &lt;OccurrenceDateTime&gt;\n" +
            "                        &lt;ns2:DateTime&gt;2018-05-18T14:30:11.615Z&lt;/ns2:DateTime&gt;\n" +
            "                    &lt;/OccurrenceDateTime&gt;\n" +
            "                &lt;/SpecifiedSalesEvent&gt;\n" +
            "                &lt;SpecifiedFishingActivity&gt;\n" +
            "                    &lt;TypeCode&gt;LAN&lt;/TypeCode&gt;\n" +
            "                    &lt;RelatedFLUXLocation&gt;\n" +
            "                        &lt;TypeCode listID=&quot;FLUX_LOCATION_TYPE&quot;&gt;LOCATION&lt;/TypeCode&gt;\n" +
            "                        &lt;CountryID schemeID=&quot;TERRITORY&quot;&gt;IRL&lt;/CountryID&gt;\n" +
            "                        &lt;ID schemeID=&quot;LOCATION&quot;&gt;IEBAL&lt;/ID&gt;\n" +
            "                    &lt;/RelatedFLUXLocation&gt;\n" +
            "                    &lt;SpecifiedDelimitedPeriod&gt;\n" +
            "                        &lt;StartDateTime&gt;\n" +
            "                            &lt;ns2:DateTime&gt;2018-05-18T09:30:11.615Z&lt;/ns2:DateTime&gt;\n" +
            "                        &lt;/StartDateTime&gt;\n" +
            "                    &lt;/SpecifiedDelimitedPeriod&gt;\n" +
            "                    &lt;SpecifiedFishingTrip&gt;\n" +
            "                        &lt;ID schemeID=&quot;EU_TRIP_ID&quot;&gt;IRL-TRP-2018-67298955&lt;/ID&gt;\n" +
            "                    &lt;/SpecifiedFishingTrip&gt;\n" +
            "                    &lt;RelatedVesselTransportMeans&gt;\n" +
            "                        &lt;ID schemeID=&quot;CFR&quot;&gt;IRL332836737&lt;/ID&gt;\n" +
            "                        &lt;Name&gt;De Duiker&lt;/Name&gt;\n" +
            "                        &lt;RegistrationVesselCountry&gt;\n" +
            "                            &lt;ID schemeID=&quot;TERRITORY&quot;&gt;IRL&lt;/ID&gt;\n" +
            "                        &lt;/RegistrationVesselCountry&gt;\n" +
            "                        &lt;SpecifiedContactParty&gt;\n" +
            "                            &lt;RoleCode listID=&quot;FLUX_CONTACT_ROLE&quot;&gt;MASTER&lt;/RoleCode&gt;\n" +
            "                            &lt;SpecifiedContactPerson&gt;\n" +
            "                                &lt;GivenName&gt;Kris&lt;/GivenName&gt;\n" +
            "                                &lt;MiddleName&gt;Kris&lt;/MiddleName&gt;\n" +
            "                                &lt;FamilyName&gt;Bult???&lt;/FamilyName&gt;\n" +
            "                            &lt;/SpecifiedContactPerson&gt;\n" +
            "                        &lt;/SpecifiedContactParty&gt;\n" +
            "                    &lt;/RelatedVesselTransportMeans&gt;\n" +
            "                &lt;/SpecifiedFishingActivity&gt;\n" +
            "                &lt;SpecifiedFLUXLocation&gt;\n" +
            "                    &lt;TypeCode listID=&quot;FLUX_LOCATION_TYPE&quot;&gt;LOCATION&lt;/TypeCode&gt;\n" +
            "                    &lt;CountryID schemeID=&quot;TERRITORY&quot;&gt;BEL&lt;/CountryID&gt;\n" +
            "                    &lt;ID schemeID=&quot;LOCATION&quot;&gt;BEZEE&lt;/ID&gt;\n" +
            "                &lt;/SpecifiedFLUXLocation&gt;\n" +
            "                &lt;SpecifiedSalesParty&gt;\n" +
            "                    &lt;ID schemeID=&quot;MS&quot;&gt;6980955688&lt;/ID&gt;\n" +
            "                    &lt;Name&gt;Van Impe&lt;/Name&gt;\n" +
            "                    &lt;RoleCode listID=&quot;FLUX_SALES_PARTY_ROLE&quot;&gt;SENDER&lt;/RoleCode&gt;\n" +
            "                &lt;/SpecifiedSalesParty&gt;\n" +
            "                &lt;SpecifiedSalesParty&gt;\n" +
            "                    &lt;ID schemeID=&quot;VAT&quot;&gt;9311215987&lt;/ID&gt;\n" +
            "                    &lt;Name&gt;Van Impe&lt;/Name&gt;\n" +
            "                    &lt;RoleCode listID=&quot;FLUX_SALES_PARTY_ROLE&quot;&gt;BUYER&lt;/RoleCode&gt;\n" +
            "                &lt;/SpecifiedSalesParty&gt;\n" +
            "                &lt;SpecifiedSalesParty&gt;\n" +
            "                    &lt;Name&gt;Van Impe&lt;/Name&gt;\n" +
            "                    &lt;RoleCode listID=&quot;FLUX_SALES_PARTY_ROLE&quot;&gt;PROVIDER&lt;/RoleCode&gt;\n" +
            "                &lt;/SpecifiedSalesParty&gt;\n" +
            "            &lt;/IncludedSalesDocument&gt;\n" +
            "        &lt;/ns3:SalesReport&gt;\n" +
            "    &lt;/ns4:FLUXSalesReportMessage&gt;\n" +
            "&lt;/ns4:Report&gt;" +
            "    </report>\n" +
            "</ns2:SendSalesReportRequest>\n";


    private static final String EXPECTED_OUTGOING_MESSAGE = "<ns3:FLUXSalesReportMessage xmlns=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" xmlns:ns2=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" xmlns:ns3=\"urn:un:unece:uncefact:data:standard:FLUXSalesReportMessage:3\"><ns3:FLUXReportDocument><ID schemeID=\"UUID\">9786fe08-5476-4cac-b069-90271f8e77b0</ID><CreationDateTime><ns2:DateTime>2018-05-18T19:30:11Z</ns2:DateTime></CreationDateTime><PurposeCode listID=\"FLUX_GP_PURPOSE\">9</PurposeCode><Purpose>Sales note example</Purpose><OwnerFLUXParty><ID schemeID=\"FLUX_GP_PARTY\">BEL</ID></OwnerFLUXParty></ns3:FLUXReportDocument><ns3:SalesReport><ItemTypeCode listID=\"FLUX_SALES_TYPE\">SN</ItemTypeCode><IncludedSalesDocument><ID schemeID=\"EU_SALES_ID\">BEL-SN-2018-690875</ID><CurrencyCode listID=\"TERRITORY_CURR\">EUR</CurrencyCode><SpecifiedSalesBatch><SpecifiedAAPProduct><SpeciesCode listID=\"FAO_SPECIES\">ADI</SpeciesCode><WeightMeasure unitCode=\"KGM\">5285</WeightMeasure><UsageCode listID=\"PROD_USAGE\">HCN</UsageCode><AppliedAAPProcess><TypeCode listID=\"FISH_FRESHNESS\">B</TypeCode><TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode><TypeCode listID=\"FISH_PRESENTATION\">SGT</TypeCode></AppliedAAPProcess><TotalSalesPrice><ChargeAmount>1368.57</ChargeAmount></TotalSalesPrice><SpecifiedSizeDistribution><CategoryCode listID=\"FISH_SIZE_CATEGORY\">7b</CategoryCode><ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode></SpecifiedSizeDistribution><OriginFLUXLocation><TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode><ID schemeID=\"FAO_AREA\">27.3.d.24</ID></OriginFLUXLocation></SpecifiedAAPProduct><SpecifiedAAPProduct><SpeciesCode listID=\"FAO_SPECIES\">ACG</SpeciesCode><WeightMeasure unitCode=\"KGM\">7338</WeightMeasure><UsageCode listID=\"PROD_USAGE\">HCN-INDIRECT</UsageCode><AppliedAAPProcess><TypeCode listID=\"FISH_FRESHNESS\">V</TypeCode><TypeCode listID=\"FISH_PRESERVATION\">DRI</TypeCode><TypeCode listID=\"FISH_PRESENTATION\">WNG</TypeCode></AppliedAAPProcess><TotalSalesPrice><ChargeAmount>7778.56</ChargeAmount></TotalSalesPrice><SpecifiedSizeDistribution><CategoryCode listID=\"FISH_SIZE_CATEGORY\">N/A</CategoryCode><ClassCode listID=\"FISH_SIZE_CLASS\">BMS</ClassCode></SpecifiedSizeDistribution><OriginFLUXLocation><TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode><ID schemeID=\"FAO_AREA\">27.3.d.24</ID></OriginFLUXLocation></SpecifiedAAPProduct><SpecifiedAAPProduct><SpeciesCode listID=\"FAO_SPECIES\">AAB</SpeciesCode><WeightMeasure unitCode=\"KGM\">4876</WeightMeasure><UsageCode listID=\"PROD_USAGE\">HCN-INDIRECT</UsageCode><AppliedAAPProcess><TypeCode listID=\"FISH_FRESHNESS\">B</TypeCode><TypeCode listID=\"FISH_PRESERVATION\">DRI</TypeCode><TypeCode listID=\"FISH_PRESENTATION\">SGT</TypeCode></AppliedAAPProcess><TotalSalesPrice><ChargeAmount>1368.57</ChargeAmount></TotalSalesPrice><SpecifiedSizeDistribution><CategoryCode listID=\"FISH_SIZE_CATEGORY\">N/A</CategoryCode><ClassCode listID=\"FISH_SIZE_CLASS\">BMS</ClassCode></SpecifiedSizeDistribution><OriginFLUXLocation><TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode><ID schemeID=\"FAO_AREA\">27.3.d.24</ID></OriginFLUXLocation></SpecifiedAAPProduct><SpecifiedAAPProduct><SpeciesCode listID=\"FAO_SPECIES\">ADP</SpeciesCode><WeightMeasure unitCode=\"KGM\">7759</WeightMeasure><UsageCode listID=\"PROD_USAGE\">IND</UsageCode><AppliedAAPProcess><TypeCode listID=\"FISH_FRESHNESS\">V</TypeCode><TypeCode listID=\"FISH_PRESERVATION\">ALI</TypeCode><TypeCode listID=\"FISH_PRESENTATION\">CLA</TypeCode></AppliedAAPProcess><TotalSalesPrice><ChargeAmount>2292.13</ChargeAmount></TotalSalesPrice><SpecifiedSizeDistribution><CategoryCode listID=\"FISH_SIZE_CATEGORY\">N/A</CategoryCode><ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode></SpecifiedSizeDistribution><OriginFLUXLocation><TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode><ID schemeID=\"FAO_AREA\">27.3.d.24</ID></OriginFLUXLocation></SpecifiedAAPProduct><SpecifiedAAPProduct><SpeciesCode listID=\"FAO_SPECIES\">ABE</SpeciesCode><WeightMeasure unitCode=\"KGM\">8822</WeightMeasure><UsageCode listID=\"PROD_USAGE\">HCN-INDIRECT</UsageCode><AppliedAAPProcess><TypeCode listID=\"FISH_FRESHNESS\">E</TypeCode><TypeCode listID=\"FISH_PRESERVATION\">BOI</TypeCode><TypeCode listID=\"FISH_PRESENTATION\">LVR-C</TypeCode></AppliedAAPProcess><TotalSalesPrice><ChargeAmount>600.83</ChargeAmount></TotalSalesPrice><SpecifiedSizeDistribution><CategoryCode listID=\"FISH_SIZE_CATEGORY\">N/A</CategoryCode><ClassCode listID=\"FISH_SIZE_CLASS\">BMS</ClassCode></SpecifiedSizeDistribution><OriginFLUXLocation><TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode><ID schemeID=\"FAO_AREA\">27.3.d.24</ID></OriginFLUXLocation></SpecifiedAAPProduct><SpecifiedAAPProduct><SpeciesCode listID=\"FAO_SPECIES\">ACP</SpeciesCode><WeightMeasure unitCode=\"KGM\">8079</WeightMeasure><UsageCode listID=\"PROD_USAGE\">HCN-INDIRECT</UsageCode><AppliedAAPProcess><TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode><TypeCode listID=\"FISH_PRESERVATION\">DRI</TypeCode><TypeCode listID=\"FISH_PRESENTATION\">HEA</TypeCode></AppliedAAPProcess><TotalSalesPrice><ChargeAmount>3626.72</ChargeAmount></TotalSalesPrice><SpecifiedSizeDistribution><CategoryCode listID=\"FISH_SIZE_CATEGORY\">N/A</CategoryCode><ClassCode listID=\"FISH_SIZE_CLASS\">BMS</ClassCode></SpecifiedSizeDistribution><OriginFLUXLocation><TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode><ID schemeID=\"FAO_AREA\">27.3.d.24</ID></OriginFLUXLocation></SpecifiedAAPProduct></SpecifiedSalesBatch><SpecifiedSalesEvent><OccurrenceDateTime><ns2:DateTime>2018-05-18T14:30:11Z</ns2:DateTime></OccurrenceDateTime></SpecifiedSalesEvent><SpecifiedFishingActivity><TypeCode>LAN</TypeCode><RelatedFLUXLocation><TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</TypeCode><CountryID schemeID=\"TERRITORY\">IRL</CountryID><ID schemeID=\"LOCATION\">IEBAL</ID></RelatedFLUXLocation><SpecifiedDelimitedPeriod><StartDateTime><ns2:DateTime>2018-05-18T09:30:11Z</ns2:DateTime></StartDateTime></SpecifiedDelimitedPeriod><SpecifiedFishingTrip><ID schemeID=\"EU_TRIP_ID\">IRL-TRP-2018-67298955</ID></SpecifiedFishingTrip><RelatedVesselTransportMeans><ID schemeID=\"CFR\">IRL332836737</ID><Name>De Duiker</Name><RegistrationVesselCountry><ID schemeID=\"TERRITORY\">IRL</ID></RegistrationVesselCountry><SpecifiedContactParty><RoleCode listID=\"FLUX_CONTACT_ROLE\">MASTER</RoleCode><SpecifiedContactPerson><GivenName>Kris</GivenName><MiddleName>Kris</MiddleName><FamilyName>Bult???</FamilyName></SpecifiedContactPerson></SpecifiedContactParty></RelatedVesselTransportMeans></SpecifiedFishingActivity><SpecifiedFLUXLocation><TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</TypeCode><CountryID schemeID=\"TERRITORY\">BEL</CountryID><ID schemeID=\"LOCATION\">BEZEE</ID></SpecifiedFLUXLocation><SpecifiedSalesParty><ID schemeID=\"MS\">6980955688</ID><Name>Van Impe</Name><RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">SENDER</RoleCode></SpecifiedSalesParty><SpecifiedSalesParty><ID schemeID=\"VAT\">9311215987</ID><Name>Van Impe</Name><RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">BUYER</RoleCode></SpecifiedSalesParty><SpecifiedSalesParty><Name>Van Impe</Name><RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">PROVIDER</RoleCode></SpecifiedSalesParty></IncludedSalesDocument></ns3:SalesReport></ns3:FLUXSalesReportMessage>";
}
