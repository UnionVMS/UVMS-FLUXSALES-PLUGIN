package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.helper;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;

@Stateless
@Slf4j
public class ResponseHelper {

    public String getGuidOrNull(FLUXSalesResponseMessage response) {
        try {
            return response.getFLUXResponseDocument()
                    .getIDS()
                    .get(0)
                    .getValue();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            log.error("Invalid response. Does not contain a guid.", e);
            return null;
        }
    }

}
