package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.bean;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationProblem;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;

import javax.ejb.Stateless;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

@Stateless
@Slf4j
public class XsdValidatorService {

    public ValidationResult doesMessagePassXsdValidation(Element incomingXML) {
        try {
            StringWriter writer = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(incomingXML), new StreamResult(writer));
            String incomingXMLAsString = writer.toString();

            // If the incoming message is a response, we don't do XSD validation.
            if (incomingXMLAsString.contains("FLUXSalesResponseMessage")) {
                log.info("Skipping XSD validation because a response was received");
                return new ValidationResult(true, Lists.<ValidationProblem>newArrayList());
            }

            StringReader reader = new StringReader(incomingXMLAsString);
            Source xmlFile = new StreamSource(reader);

            return validate(xmlFile);
        } catch (TransformerException e) {
            throw new RuntimeException("Couldn't transform Element to Source", e);
        }
    }

    public ValidationResult validate(Source source) {
        Validator validator = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
        validator.setSchemaSources((Source[]) getXSDSources().toArray());
        return validator.validateInstance(source);
    }

    private List<StreamSource> getXSDSources() {
        return Arrays.asList(
                getSource("/sales/UnqualifiedDataType_20p0.xsd"),
                getSource("/sales/codelist_standard_UNECE_CommunicationMeansTypeCode_D16A.xsd"),
                getSource("/sales/QualifiedDataType_20p0.xsd"),
                getSource("/sales/ReusableAggregateBusinessInformationEntity_20p0.xsd"),
                getSource("/sales/FLUXSalesReportMessage_3p0.xsd"),
                getSource("/sales/FLUXSalesQueryMessage_3p0.xsd"));
    }

    private StreamSource getSource(String s) {
        return new StreamSource(getClass().getResourceAsStream(s));
    }

}
