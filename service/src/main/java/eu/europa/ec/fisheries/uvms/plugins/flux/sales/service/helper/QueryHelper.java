package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.helper;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesQueryMessage;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;

@Stateless
@Slf4j
public class QueryHelper {

    public String getGuidOrNull(FLUXSalesQueryMessage query) {
        try {
            return query.getSalesQuery()
                    .getID()
                    .getValue();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            log.error("Invalid report. Does not contain a guid.", e);
            return null;
        }
    }

    public String getCountryOfSenderOrNull(FLUXSalesQueryMessage query) {
        try {
            return query.getSalesQuery()
                    .getSubmitterFLUXParty()
                    .getIDS()
                    .get(0)
                    .getValue();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            log.error("Invalid report. Does not contain a country of sender.", e);
            return null;
        }
    }

}
