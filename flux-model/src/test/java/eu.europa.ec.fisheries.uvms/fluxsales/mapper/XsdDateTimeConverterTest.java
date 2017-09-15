package eu.europa.ec.fisheries.uvms.fluxsales.mapper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static eu.europa.ec.fisheries.uvms.fluxsales.mapper.XsdDateTimeConverter.marshalDate;
import static eu.europa.ec.fisheries.uvms.fluxsales.mapper.XsdDateTimeConverter.marshalDateTime;
import static eu.europa.ec.fisheries.uvms.fluxsales.mapper.XsdDateTimeConverter.unmarshal;
import static org.junit.Assert.assertEquals;

public class XsdDateTimeConverterTest {

    private final String TIME = "2017-05-11T12:10:38+02:00";
    private final String DATE = "2017-05-11+02:00";

    @Test
    public void testUnmarshal() throws Exception {
        DateTime time = unmarshal(TIME);

        assertEquals(2017, time.year().get());
        assertEquals(5, time.monthOfYear().get());
        assertEquals(11, time.dayOfMonth().get());
        assertEquals(12, time.hourOfDay().get());
        assertEquals(10, time.minuteOfHour().get());
        assertEquals(38, time.secondOfMinute().get());
        assertEquals(DateTimeZone.forOffsetHours(2), time.getZone());
    }

    @Test
    public void testMarshalDate() throws Exception {
        String marshalledDate = marshalDate(DateTime.parse(TIME));

        assertEquals(DATE, marshalledDate);
    }

    @Test
    public void testMarshalDateTime() throws Exception {
        String marshalledDate = marshalDateTime(DateTime.parse(TIME));

        assertEquals(TIME, marshalledDate);
    }

}