package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({TransformerFactory.class})
@PowerMockIgnore( {"javax.management.*"})
public class XsdValidatorServiceTest {

    private XsdValidatorService xsdValidator;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        xsdValidator = new XsdValidatorService();
    }


    @Test
    public void testValidateWithValidXML() {
        assertTrue(xsdValidator.validate(getSource("/sales/validXML.xml")).isValid());
    }

    @Test
    public void testValidateWithInvalidXML() {
        assertFalse(xsdValidator.validate(getSource("/sales/invalidXML.xml")).isValid());
    }

    @Test
    public void testValidateWhenSourceIsNull() {
        exception.expect(NullPointerException.class);
        exception.expectMessage("Source parameter cannot be null");

        xsdValidator.validate(null);
    }

    @Test
    public void testDoesMessagePassXsdValidation() throws Exception {
        mockStatic(TransformerFactory.class);
        Element element = mock(Element.class);
        TransformerFactory transformerFactory = mock(TransformerFactory.class);
        Transformer transformer = mock(Transformer.class);

        when(TransformerFactory.newInstance()).thenReturn(transformerFactory);
        doReturn(transformer).when(transformerFactory).newTransformer();

        assertFalse(xsdValidator.doesMessagePassXsdValidation(element).isValid());
        verify(transformer).transform(any(DOMSource.class), any(StreamResult.class));
    }

//    @Test
//    public void testDoesMessagePassXsdValidationWhenTransformerExceptionWasThrown() throws Exception {
//        mockStatic(TransformerFactory.class);
//        Element element = mock(Element.class);
//        TransformerFactory transformerFactory = mock(TransformerFactory.class);
//        Transformer transformer = mock(Transformer.class);
//
//        when(TransformerFactory.newInstance()).thenReturn(transformerFactory);
//        doReturn(transformer).when(transformerFactory).newTransformer();
//        doThrow(new TransformerException("")).when(transformer).transform(any(DOMSource.class), any(StreamResult.class));
//
//        assertFalse(xsdValidator.doesMessagePassXsdValidation(element).isValid());
//        verify(transformer).transform(any(DOMSource.class), any(StreamResult.class));
//    }

    private StreamSource getSource(String s) {
        return new StreamSource(getClass().getResourceAsStream(s));
    }

}