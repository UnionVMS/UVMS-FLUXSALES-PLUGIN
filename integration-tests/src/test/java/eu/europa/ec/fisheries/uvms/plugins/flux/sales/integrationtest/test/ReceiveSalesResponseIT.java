package eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.test;

import eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.deployment.TestOnGoodWorkingPlugin;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.test.state.ReceivedMessagesInExchange;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.webservice.FluxMessageReceiverBean;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import xeu.bridge_connector.v1.Connector2BridgeRequest;
import xeu.bridge_connector.v1.VerbosityType;

import javax.ejb.EJB;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class ReceiveSalesResponseIT extends TestOnGoodWorkingPlugin {

	@EJB
	private StartupBean startupBean;

	@EJB
    private FluxMessageReceiverBean fluxMessageReceiverBean;

	@EJB
    private ReceivedMessagesInExchange receivedMessagesInExchange;

	@Before @After
    public void clear() {
        receivedMessagesInExchange.clear();
    }

	@Test
	@OperateOnDeployment("good-working-plugin")
	public void pluginReceivesAMessageAndSendsItExchangeSuccessfully() throws Exception {
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

        //send message
        Connector2BridgeRequest request = createRequest();
        fluxMessageReceiverBean.post(request);

        //wait until message is sent through to Exchange
        await().atMost(30, SECONDS)
                .until(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return !receivedMessagesInExchange.getAll().isEmpty();
                    }
                });

        //assert message content
        assertEquals(1, receivedMessagesInExchange.getAll().size());
        String actualMessage = receivedMessagesInExchange.getAll().get(0);
        assertTrue(actualMessage.startsWith(EXPECTED_MESSAGE_BEFORE_CURRENT_DATE));
        assertTrue(actualMessage.endsWith(EXPECTED_MESSAGE_AFTER_CURRENT_DATE));
	}

    private Connector2BridgeRequest createRequest() {
        Connector2BridgeRequest request = new Connector2BridgeRequest();
        request.setON("12345678901234567890");
        request.setDF("urn:un:unece:uncefact:fisheries:FLUX:SALES:EU:2");
        request.setAny(marshalToDOM(SALES_RESPONSE));
        request.setAD("BEL");
        request.setAR(false);
        request.setTO(200);
        request.setTODT(new DateTime(2018, 1, 12, 13, 14));
        request.setVB(VerbosityType.ERROR);
        request.getOtherAttributes().put(new QName("FR"), "NLD");
        return request;
    }

    private Element marshalToDOM(String message) {
        try {
            InputStream xmlAsInputStream = new ByteArrayInputStream(message.getBytes("UTF-8"));

            javax.xml.parsers.DocumentBuilderFactory b = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            b.setNamespaceAware(true);
            DocumentBuilder db = b.newDocumentBuilder();

            Document document = db.parse(xmlAsInputStream);
            return document.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("Could not marshall message into an Element", e);
        }
    }

    private static final String EXPECTED_MESSAGE_BEFORE_CURRENT_DATE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<ns2:ReceiveSalesResponseRequest xmlns:ns2=\"urn:module.exchange.schema.fisheries.ec.europa.eu:v1\">\n" +
            "    <method>RECEIVE_SALES_RESPONSE</method>\n" +
            "    <username>FLUX</username>\n" +
            "    <pluginType>FLUX</pluginType>\n" +
            "    <senderOrReceiver>NLD</senderOrReceiver>\n" +
            "    <messageGuid>409ec0c1-cc61-40b1-a476-1501e29f6326</messageGuid>\n" +
            "    <date>";

	private static final String EXPECTED_MESSAGE_AFTER_CURRENT_DATE = "</date>\n" +
            "    <onValue>12345678901234567890</onValue>\n" +
            "    <response>&lt;?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?&gt;\n" +
            "&lt;ns3:FLUXSalesResponseMessage xmlns=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" xmlns:ns2=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" xmlns:ns3=\"eu.europa.ec.fisheries.schema.sales.flux\"&gt;\n" +
            "    &lt;ns3:FLUXResponseDocument&gt;\n" +
            "        &lt;ID schemeID=\"UUID\"&gt;409ec0c1-cc61-40b1-a476-1501e29f6326&lt;/ID&gt;\n" +
            "        &lt;ReferencedID schemeID=\"UUID\"&gt;412adff1-a4b8-4877-89f7-03ae4292d5dd&lt;/ReferencedID&gt;\n" +
            "        &lt;CreationDateTime&gt;\n" +
            "            &lt;ns2:DateTime&gt;2017-05-11T14:09:22Z&lt;/ns2:DateTime&gt;\n" +
            "        &lt;/CreationDateTime&gt;\n" +
            "        &lt;ResponseCode listID=\"FLUX_GP_RESPONSE\"&gt;OK&lt;/ResponseCode&gt;\n" +
            "        &lt;RespondentFLUXParty&gt;\n" +
            "            &lt;ID schemeID=\"FLUX_GP_PARTY\"&gt;NLD&lt;/ID&gt;\n" +
            "        &lt;/RespondentFLUXParty&gt;\n" +
            "    &lt;/ns3:FLUXResponseDocument&gt;\n" +
            "&lt;/ns3:FLUXSalesResponseMessage&gt;\n" +
            "</response>\n" +
            "</ns2:ReceiveSalesResponseRequest>\n";

	private static final String SALES_RESPONSE = "<FLUXSalesResponseMessage xmlns:clm63155CommunicationChannelCode=\"urn:un:unece:uncefact:codelist:standard:UNECE:CommunicationMeansTypeCode:D16A\" xmlns:qdt=\"urn:un:unece:uncefact:data:Standard:QualifiedDataType:20\" xmlns:ram=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" xmlns:udt=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" xsi:schemaLocation=\"urn:un:unece:uncefact:data:standard:FLUXSalesResponseMessage:3 FLUXSalesResponseMessage_3p1.xsd\" xmlns=\"urn:un:unece:uncefact:data:standard:FLUXSalesResponseMessage:3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "\t\t\t<FLUXResponseDocument>\n" +
            "\t\t\t\t<ram:ID schemeID=\"UUID\">409ec0c1-cc61-40b1-a476-1501e29f6326</ram:ID>\n" +
            "\t\t\t\t<ram:ReferencedID schemeID=\"UUID\">412adff1-a4b8-4877-89f7-03ae4292d5dd</ram:ReferencedID>\n" +
            "\t\t\t\t<ram:CreationDateTime>\n" +
            "\t\t\t\t\t<udt:DateTime>2017-05-11T14:09:22Z</udt:DateTime>\n" +
            "\t\t\t\t</ram:CreationDateTime>\n" +
            "\t\t\t\t<ram:ResponseCode listID=\"FLUX_GP_RESPONSE\">OK</ram:ResponseCode>\n" +
            "\t\t\t\t<ram:RespondentFLUXParty>\n" +
            "\t\t\t\t\t<ram:ID schemeID=\"FLUX_GP_PARTY\">NLD</ram:ID>\n" +
            "\t\t\t\t</ram:RespondentFLUXParty>\n" +
            "\t\t\t</FLUXResponseDocument>\n" +
            "\t\t</FLUXSalesResponseMessage>";
}
