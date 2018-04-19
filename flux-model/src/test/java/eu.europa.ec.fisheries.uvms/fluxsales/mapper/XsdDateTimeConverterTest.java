package eu.europa.ec.fisheries.uvms.fluxsales.mapper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static eu.europa.ec.fisheries.uvms.fluxsales.mapper.XsdDateTimeConverter.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class XsdDateTimeConverterTest {

    private final String TIME = "2017-05-11T10:10:38Z";
    private final String DATE = "2017-05-11Z";

    @Test
    public void testUnmarshal() throws Exception {
        DateTime time = unmarshal(TIME);

        assertEquals(2017, time.year().get());
        assertEquals(5, time.monthOfYear().get());
        assertEquals(11, time.dayOfMonth().get());
        assertEquals(10, time.hourOfDay().get());
        assertEquals(10, time.minuteOfHour().get());
        assertEquals(38, time.secondOfMinute().get());
        assertEquals(DateTimeZone.UTC, time.getZone());
    }

    @Test
    public void testUnmarshallWhenArgumentIsNull() {
        assertNull(unmarshal(null));
    }

    @Test
    public void testMarshalDate() throws Exception {
        String marshalledDate = marshalDate(DateTime.parse(TIME));

        assertEquals(DATE, marshalledDate);
    }

    @Test
    public void testMarshalDateWhenArgumentIsNull() {
        assertNull(marshalDate(null));
    }

    @Test
    public void testMarshalDateTime() throws Exception {
        String marshalledDate = marshalDateTime(DateTime.parse(TIME));

        assertEquals(TIME, marshalledDate);
    }

    @Test
    public void testMarshalDateTimeWhenArgumentIsNull() {
        assertNull(marshalDateTime(null));
    }

}