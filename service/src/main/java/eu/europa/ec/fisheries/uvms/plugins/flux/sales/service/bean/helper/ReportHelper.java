package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.sales.Report;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;

@Stateless
@Slf4j
public class ReportHelper {

    public String getGuidOrNull(Report report) {
        try {
            return report.getFLUXSalesReportMessage()
                    .getFLUXReportDocument()
                    .getIDS()
                    .get(0)
                    .getValue();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            log.error("Invalid report. Does not contain a guid.", e);
            return null;
        }
    }

}
