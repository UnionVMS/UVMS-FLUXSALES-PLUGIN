package eu.europa.ec.fisheries.uvms.plugins.flux.service;

import eu.europa.ec.fisheries.uvms.plugins.flux.framework.XMLResourceLoader;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import xeu.bridge_connector.v1.RequestType;

import static org.junit.Assert.assertEquals;

public class RequestTypeHelperTest {

    private RequestTypeHelper requestTypeHelper;

    private Element data;

    private XMLResourceLoader xmlResourceLoader;

    @Before
    public void setUp() throws Exception {
        requestTypeHelper = new RequestTypeHelper();
        xmlResourceLoader = new XMLResourceLoader();

        data = xmlResourceLoader.loadFileAsElement("test_xml_document");
    }

    @Test
    public void determineMessageType() throws Exception {
        RequestType type = new RequestType();
        type.setAny(data);

        String messageType = requestTypeHelper.determineMessageType(type);
        assertEquals("FLUXSalesReportMessage", messageType);
    }
}