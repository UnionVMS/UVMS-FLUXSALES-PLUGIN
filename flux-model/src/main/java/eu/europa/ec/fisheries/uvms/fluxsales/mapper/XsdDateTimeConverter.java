package eu.europa.ec.fisheries.uvms.fluxsales.mapper;

import org.joda.time.DateTime;

import javax.xml.bind.DatatypeConverter;

public class XsdDateTimeConverter {
    public static DateTime unmarshal(String dateTime) {
        if (dateTime != null) {
            return new DateTime(DatatypeConverter.parseDate(dateTime));
        } else {
            return null;
        }
    }

    public static String marshalDate(DateTime dateTime) {
        if (dateTime != null) {
            return DatatypeConverter.printDate(dateTime.toGregorianCalendar());
        } else {
            return null;
        }
    }

    public static String marshalDateTime(DateTime dateTime) {
        if (dateTime != null) {
            return DatatypeConverter.printDateTime(dateTime.toGregorianCalendar());
        } else {
            return null;
        }
    }
}
