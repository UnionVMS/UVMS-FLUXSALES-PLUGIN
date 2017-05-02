package eu.europa.ec.fisheries.uvms.plugins.flux.framework;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class XMLResourceLoader {

    private Document loadXMLFileAndParseToElement(InputStream data) throws IOException, ParserConfigurationException, SAXException {
        final String xmlDocumentAsString = IOUtils.toString(data);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(new InputSource(new StringReader(xmlDocumentAsString)));
    }

    public Element loadFileAsElement(String path) throws ParserConfigurationException, SAXException, IOException {
        InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        return loadXMLFileAndParseToElement(xml).getDocumentElement();
    }
}
