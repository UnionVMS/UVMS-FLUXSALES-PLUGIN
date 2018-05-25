package eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.test;

import eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.deployment.TestOnGoodWorkingPlugin;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.mock.PortMock;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.test.producer.EventBusMessageProducer;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.StartupBean;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
public class SendSalesResponseIT extends TestOnGoodWorkingPlugin {

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
        assertEquals("FRA", actualMessage.getAD());
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

    private static final String MESSAGE_FROM_EXCHANGE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<ns2:SendSalesResponseRequest xmlns:ns2=\"urn:plugin.exchange.schema.fisheries.ec.europa.eu:v1\">\n" +
            "    <method>SEND_SALES_RESPONSE</method>\n" +
            "    <response>&lt;?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?&gt;\n" +
            "&lt;ns3:FLUXSalesResponseMessage xmlns=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" xmlns:ns2=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" xmlns:ns3=\"eu.europa.ec.fisheries.schema.sales.flux\"&gt;\n" +
            "    &lt;ns3:FLUXResponseDocument&gt;\n" +
            "        &lt;ID schemeID=\"UUID\"&gt;5d6ea308-53af-4fb9-a0a4-4e4496a824ee&lt;/ID&gt;\n" +
            "        &lt;ReferencedID schemeID=\"UUID\"&gt;23b098af-e300-4369-bc7a-9b207305efdd&lt;/ReferencedID&gt;\n" +
            "        &lt;CreationDateTime&gt;\n" +
            "            &lt;ns2:DateTime&gt;2018-04-24T11:44:12.359Z&lt;/ns2:DateTime&gt;\n" +
            "        &lt;/CreationDateTime&gt;\n" +
            "        &lt;ResponseCode listID=\"FLUX_GP_RESPONSE\"&gt;NOK&lt;/ResponseCode&gt;\n" +
            "        &lt;RelatedValidationResultDocument&gt;\n" +
            "            &lt;ValidatorID&gt;BEL&lt;/ValidatorID&gt;\n" +
            "            &lt;CreationDateTime&gt;\n" +
            "                &lt;ns2:DateTime&gt;2018-04-24T11:44:12.358Z&lt;/ns2:DateTime&gt;\n" +
            "            &lt;/CreationDateTime&gt;\n" +
            "            &lt;RelatedValidationQualityAnalysis&gt;\n" +
            "                &lt;LevelCode listID=\"FLUX_GP_VALIDATION_LEVEL\"&gt;L03&lt;/LevelCode&gt;\n" +
            "                &lt;TypeCode listID=\"FLUX_GP_VALIDATION_TYPE\"&gt;WAR&lt;/TypeCode&gt;\n" +
            "                &lt;Result&gt;The report document identifier already exists&lt;/Result&gt;\n" +
            "                &lt;ID schemeID=\"SALE_BR\"&gt;SALE-L03-00-0010&lt;/ID&gt;\n" +
            "                &lt;ReferencedItem&gt;//*[local-name()='fluxSalesReportMessage']//*[local-name()='fluxReportDocument']//*[local-name()='ids']&lt;/ReferencedItem&gt;\n" +
            "            &lt;/RelatedValidationQualityAnalysis&gt;\n" +
            "            &lt;RelatedValidationQualityAnalysis&gt;\n" +
            "                &lt;LevelCode listID=\"FLUX_GP_VALIDATION_LEVEL\"&gt;L02&lt;/LevelCode&gt;\n" +
            "                &lt;TypeCode listID=\"FLUX_GP_VALIDATION_TYPE\"&gt;ERR&lt;/TypeCode&gt;\n" +
            "                &lt;Result&gt;Sales Event date &amp;gt;= Start date (landing)&lt;/Result&gt;\n" +
            "                &lt;ID schemeID=\"SALE_BR\"&gt;SALE-L02-00-0050&lt;/ID&gt;\n" +
            "                &lt;ReferencedItem&gt;&lt;/ReferencedItem&gt;\n" +
            "                &lt;ReferencedItem&gt;&lt;/ReferencedItem&gt;\n" +
            "            &lt;/RelatedValidationQualityAnalysis&gt;\n" +
            "            &lt;RelatedValidationQualityAnalysis&gt;\n" +
            "                &lt;LevelCode listID=\"FLUX_GP_VALIDATION_LEVEL\"&gt;L03&lt;/LevelCode&gt;\n" +
            "                &lt;TypeCode listID=\"FLUX_GP_VALIDATION_TYPE\"&gt;ERR&lt;/TypeCode&gt;\n" +
            "                &lt;Result&gt;The vessel is not in the EU fleet under the flag state at landing date&lt;/Result&gt;\n" +
            "                &lt;ID schemeID=\"SALE_BR\"&gt;SALE-L03-00-0130&lt;/ID&gt;\n" +
            "                &lt;ReferencedItem&gt;(((//*[local-name()='fluxSalesReportMessage']//*[local-name()='salesReports'])[0]//*[local-name()='includedSalesDocuments'])[0]//*[local-name()='specifiedFishingActivities'])[0]//*[local-name()='relatedVesselTransportMeans']&lt;/ReferencedItem&gt;\n" +
            "                &lt;ReferencedItem&gt;(((//*[local-name()='fluxSalesReportMessage']//*[local-name()='salesReports'])[0]//*[local-name()='includedSalesDocuments'])[0]//*[local-name()='specifiedFishingActivities'])[0]//*[local-name()='specifiedDelimitedPeriods']&lt;/ReferencedItem&gt;\n" +
            "            &lt;/RelatedValidationQualityAnalysis&gt;\n" +
            "        &lt;/RelatedValidationResultDocument&gt;\n" +
            "        &lt;RespondentFLUXParty&gt;\n" +
            "            &lt;ID schemeID=\"FLUX_GP_PARTY\"&gt;BEL&lt;/ID&gt;\n" +
            "        &lt;/RespondentFLUXParty&gt;\n" +
            "    &lt;/ns3:FLUXResponseDocument&gt;\n" +
            "&lt;/ns3:FLUXSalesResponseMessage&gt;\n" +
            "</response>\n" +
            "    <recipient>FRA</recipient>\n" +
            "</ns2:SendSalesResponseRequest>\n";


    private static final String EXPECTED_OUTGOING_MESSAGE = "<ns3:FLUXSalesResponseMessage xmlns=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" xmlns:ns2=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" xmlns:ns3=\"urn:un:unece:uncefact:data:standard:FLUXSalesResponseMessage:3\"><ns3:FLUXResponseDocument><ID schemeID=\"UUID\">5d6ea308-53af-4fb9-a0a4-4e4496a824ee</ID><ReferencedID schemeID=\"UUID\">23b098af-e300-4369-bc7a-9b207305efdd</ReferencedID><CreationDateTime><ns2:DateTime>2018-04-24T11:44:12Z</ns2:DateTime></CreationDateTime><ResponseCode listID=\"FLUX_GP_RESPONSE\">NOK</ResponseCode><RelatedValidationResultDocument><ValidatorID>BEL</ValidatorID><CreationDateTime><ns2:DateTime>2018-04-24T11:44:12Z</ns2:DateTime></CreationDateTime><RelatedValidationQualityAnalysis><LevelCode listID=\"FLUX_GP_VALIDATION_LEVEL\">L03</LevelCode><TypeCode listID=\"FLUX_GP_VALIDATION_TYPE\">WAR</TypeCode><Result>The report document identifier already exists</Result><ID schemeID=\"SALE_BR\">SALE-L03-00-0010</ID><ReferencedItem>//*[local-name()='fluxSalesReportMessage']//*[local-name()='fluxReportDocument']//*[local-name()='ids']</ReferencedItem></RelatedValidationQualityAnalysis><RelatedValidationQualityAnalysis><LevelCode listID=\"FLUX_GP_VALIDATION_LEVEL\">L02</LevelCode><TypeCode listID=\"FLUX_GP_VALIDATION_TYPE\">ERR</TypeCode><Result>Sales Event date &gt;= Start date (landing)</Result><ID schemeID=\"SALE_BR\">SALE-L02-00-0050</ID><ReferencedItem></ReferencedItem><ReferencedItem></ReferencedItem></RelatedValidationQualityAnalysis><RelatedValidationQualityAnalysis><LevelCode listID=\"FLUX_GP_VALIDATION_LEVEL\">L03</LevelCode><TypeCode listID=\"FLUX_GP_VALIDATION_TYPE\">ERR</TypeCode><Result>The vessel is not in the EU fleet under the flag state at landing date</Result><ID schemeID=\"SALE_BR\">SALE-L03-00-0130</ID><ReferencedItem>(((//*[local-name()='fluxSalesReportMessage']//*[local-name()='salesReports'])[0]//*[local-name()='includedSalesDocuments'])[0]//*[local-name()='specifiedFishingActivities'])[0]//*[local-name()='relatedVesselTransportMeans']</ReferencedItem><ReferencedItem>(((//*[local-name()='fluxSalesReportMessage']//*[local-name()='salesReports'])[0]//*[local-name()='includedSalesDocuments'])[0]//*[local-name()='specifiedFishingActivities'])[0]//*[local-name()='specifiedDelimitedPeriods']</ReferencedItem></RelatedValidationQualityAnalysis></RelatedValidationResultDocument><RespondentFLUXParty><ID schemeID=\"FLUX_GP_PARTY\">BEL</ID></RespondentFLUXParty></ns3:FLUXResponseDocument></ns3:FLUXSalesResponseMessage>";
}
