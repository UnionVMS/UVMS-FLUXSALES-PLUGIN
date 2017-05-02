/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.plugins.flux.mapper;

import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.uvms.plugins.flux.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.mockdata.MockConstants;
import eu.europa.ec.fisheries.uvms.plugins.flux.mockdata.MovementTypeMock;
import junit.framework.Assert;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Element;

import un.unece.uncefact.data.standard.fluxvesselpositionmessage._4.FLUXVesselPositionMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLUXReportDocumentType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselPositionEventType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselTransportMeansType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;
import xeu.connector_bridge.v1.PostMsgType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 *
 * @author jojoha
 */
@RunWith(MockitoJUnitRunner.class)
public class FluxMessageRequestMapperTest {

    FluxMessageResponseMapper responseMapper;

    @Mock
    StartupBean settings;

    @InjectMocks
    FluxMessageRequestMapper requestMapper;

    public FluxMessageRequestMapperTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(settings.getSetting("FLUX_DEFAULT_AD")).thenReturn(MockConstants.AD);
        Mockito.when(settings.getSetting("FLUX_DATAFLOW")).thenReturn(MockConstants.FLUX_DATA_FLOW);
        Mockito.when(settings.getSetting("OWNER_FLUX_PARTY")).thenReturn(MockConstants.OWNER_FLUX_PARTY);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of mapToRequest method, of class FluxMessageRequestMapper.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMapToRequest() throws Exception {
        MovementType movement = MovementTypeMock.maptoMovementType();

        PostMsgType mapToRequest = requestMapper.mapToRequest(movement, MockConstants.GUID, MockConstants.RECIPIENT);

        Assert.assertNotNull(mapToRequest);

        assertEquals(MockConstants.RECIPIENT, mapToRequest.getAD());

        FLUXVesselPositionMessage extractVesselPositionMessage = extractVesselPositionMessage(mapToRequest.getAny());
        assertFLUXVesselPositionMessage(extractVesselPositionMessage);
        assertFluxReportDocument(extractVesselPositionMessage.getFLUXReportDocument());
        assertFluxVesselTransportMeans(extractVesselPositionMessage.getVesselTransportMeans());
        assertSpecifiedVesselPositionEvent(extractVesselPositionMessage.getVesselTransportMeans().getSpecifiedVesselPositionEvents());
    }

    @Test
    public void testMapToRequestRecipientNull() throws Exception {
        MovementType movement = MovementTypeMock.maptoMovementType();
        PostMsgType mapToRequest = requestMapper.mapToRequest(movement, MockConstants.GUID, null);
        Assert.assertNotNull(mapToRequest);
        assertEquals(MockConstants.AD, mapToRequest.getAD());
    }

    private void assertFLUXVesselPositionMessage(FLUXVesselPositionMessage message) {
        Assert.assertNotNull("FLUXVesselPositionMessage is NULL", message);
        Assert.assertNotNull("FLUXReportDocumentType is NULL", message.getFLUXReportDocument());
        Assert.assertNotNull("VesselTransportMeansType is NULL", message.getVesselTransportMeans());
    }

    private void assertFluxReportDocument(FLUXReportDocumentType fluxReportDocument) {
        Assert.assertNotNull("FLUXReportDocumentType is NULL", fluxReportDocument);

        List<IDType> ids = fluxReportDocument.getIDS();
        assertEquals(1, ids.size());

        Assert.assertTrue("Reference id is not a UUID", fluxReportDocument.getIDS().get(0).getValue().matches(MockConstants.UUID_REGEX));

        Assert.assertNotNull("DateTime is null", fluxReportDocument.getCreationDateTime().getDateTime());

        Assert.assertNotNull("Owner Flux party list size should be 1", fluxReportDocument.getOwnerFLUXParty().getIDS().size() == 1);
        assertEquals(MockConstants.OWNER_FLUX_PARTY, fluxReportDocument.getOwnerFLUXParty().getIDS().get(0).getValue());
    }

    private void assertFluxVesselTransportMeans(VesselTransportMeansType vesselTransportMeans) {
        Assert.assertNotNull("VesselTransportMeansType is NULL", vesselTransportMeans);

        assertEquals("VesselTransportMeansType id list should be of size 3", 3, vesselTransportMeans.getIDS().size());

        Map<String, String> data = new HashMap<>();
        for (IDType col : vesselTransportMeans.getIDS()) {
            data.put(col.getSchemeID(), col.getValue());
        }

        Assert.assertTrue("VesselTransportMeansType does not contain id:" + MockConstants.ASSET_IRCS, data.containsKey(MockConstants.ASSET_IRCS));
        Assert.assertTrue("VesselTransportMeansType does not contain id:" + MockConstants.ASSET_EXT_MARKING, data.containsKey(MockConstants.ASSET_EXT_MARKING));
        Assert.assertTrue("VesselTransportMeansType does not contain id:" + MockConstants.ASSET_CFR, data.containsKey(MockConstants.ASSET_CFR));

        assertEquals(MockConstants.ASSET_CFR, data.get(MockConstants.ASSET_CFR));
        assertEquals(MockConstants.ASSET_EXT_MARKING, data.get(MockConstants.ASSET_EXT_MARKING));
        assertEquals(MockConstants.ASSET_CFR, data.get(MockConstants.ASSET_CFR));

        assertEquals(MockConstants.ASSET_FLAG_STATE, vesselTransportMeans.getRegistrationVesselCountry().getID().getValue());

    }

    /**
     * Test of addHeaderValueToRequest method, of class
     * FluxMessageRequestMapper.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddHeaderValueToRequest() throws Exception {

    }

    private void assertSpecifiedVesselPositionEvent(List<VesselPositionEventType> specifiedVesselPositionEvents) {
        Assert.assertNotNull(specifiedVesselPositionEvents);
        assertEquals(1, specifiedVesselPositionEvents.size());

        VesselPositionEventType event = specifiedVesselPositionEvents.get(0);
        assertEquals(MockConstants.NOW_DATE_GREGORIAN.toString(), event.getObtainedOccurrenceDateTime().getDateTime().toString());
        assertEquals(MovementTypeType.POS.name(), event.getTypeCode().getValue());

        assertEquals(MockConstants.REPORTED_SPEED, event.getSpeedValueMeasure().getValue().doubleValue());
        assertEquals(MockConstants.REPORTED_COURSE, event.getCourseValueMeasure().getValue().doubleValue());

        assertEquals(MockConstants.LATITUDE, event.getSpecifiedVesselGeographicalCoordinate().getLatitudeMeasure().getValue().doubleValue());
        assertEquals(MockConstants.LONGITUDE, event.getSpecifiedVesselGeographicalCoordinate().getLongitudeMeasure().getValue().doubleValue());

    }

    private static FLUXVesselPositionMessage extractVesselPositionMessage(Element any) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(FLUXVesselPositionMessage.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        FLUXVesselPositionMessage xmlMessage = (FLUXVesselPositionMessage) unmarshaller.unmarshal(any);
        return xmlMessage;
    }

}
