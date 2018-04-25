package eu.europa.ec.fisheries.uvms.plugins.flux.sales.model.mapper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.xml.bind.DatatypeConverter;

public class XsdDateTimeConverter {

    private XsdDateTimeConverter() {
        //hide public constructor
    }

    public static DateTime unmarshal(String dateTime) {
        if (dateTime != null) {
            return new DateTime(DatatypeConverter.parseDate(dateTime));
        } else {
            return null;
        }
    }

    public static String marshalDate(DateTime dateTime) {
        if (dateTime != null) {
            return dateTime.withZone(DateTimeZone.UTC).toString("yyyy-MM-dd'Z'");
        } else {
            return null;
        }
    }

    public static String marshalDateTime(DateTime dateTime) {
        if (dateTime != null) {
            return dateTime.withZone(DateTimeZone.UTC).toString("yyyy-MM-dd'T'HH:mm:ss'Z'");
        } else {
            return null;
        }
    }
}

