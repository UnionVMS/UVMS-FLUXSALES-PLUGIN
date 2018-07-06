/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesQueryMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.bean.helper.QueryHelper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.bean.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.bean.helper.ResponseHelper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.producer.ExchangeEventMessageProducerBean;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.Date;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

@LocalBean
@Stateless
@Slf4j
public class ExchangeService {

    static final Logger LOG = LoggerFactory.getLogger(ExchangeService.class);

    @EJB
    ExchangeEventMessageProducerBean producer;

    @EJB
    private ReportHelper reportHelper;

    @EJB
    private ResponseHelper responseHelper;

    @EJB
    private QueryHelper queryHelper;

    public void sendSalesReportToExchange(Report report, String fr, String on) {
        try {
            String reportAsString = JAXBMarshaller.marshallJaxBObjectToString(report);
            String guid = Optional.fromNullable(reportHelper.getGuidOrNull(report))
                    .or(on);

            String text = ExchangeModuleRequestMapper.createReceiveSalesReportRequest(reportAsString, guid, fr, "FLUX", PluginType.FLUX, new Date(), on);
            producer.sendModuleMessage(text, null);

        } catch (ExchangeModelMarshallException e) {
            LOG.error("Couldn't map the sales report in the FLUX Plugin to ReceiveSalesReportRequest.", e);
        } catch (SalesMarshallException e) {
            LOG.error("Couldn't marshall the supplied sales report", e);
        } catch (Exception e) {
            LOG.error("Couldn't send sales report from the FLUX plugin to Exchange. Report is " + report, e);
        }
    }

    public void sendSalesResponseToExchange(FLUXSalesResponseMessage response, String fr, String on) {
        try {
            String responseAsString = JAXBMarshaller.marshallJaxBObjectToString(response);
            String guid = Optional  .fromNullable(responseHelper.getGuidOrNull(response))
                                    .or(on);

            String text = ExchangeModuleRequestMapper.createReceiveSalesResponseRequest(responseAsString, guid, fr,
                    new Date(), "FLUX", PluginType.FLUX, on);
            producer.sendModuleMessage(text, null);
        } catch (ExchangeModelMarshallException e) {
            LOG.error("Couldn't map the sales response in the FLUX Plugin to ReceiveSalesResponseRequest. Response is " + response, e);
        } catch (SalesMarshallException e) {
            LOG.error("Couldn't marshall the supplied sales response", e);
        } catch (Exception e) {
            LOG.error("Couldn't send sales response from the FLUX plugin to Exchange. Report is " + response, e);
        }
    }

    public void sendSalesQueryToExchange(FLUXSalesQueryMessage query, String fr, String on) {
        try {
            String queryAsString = JAXBMarshaller.marshallJaxBObjectToString(query);
            String guid = Optional  .fromNullable(queryHelper.getGuidOrNull(query))
                                    .or(on);

            String text = ExchangeModuleRequestMapper.createReceiveSalesQueryRequest(queryAsString, guid, fr, new Date(), "FLUX", PluginType.FLUX, on);
            producer.sendModuleMessage(text, null);
        } catch (ExchangeModelMarshallException e) {
            LOG.error("Couldn't map the sales query in the FLUX Plugin to ReceiveSalesQueryRequest. Query is " + query, e);
        } catch (SalesMarshallException e) {
            LOG.error("Couldn't marshall the supplied sales query", e);
        } catch (Exception e) {
            LOG.error("Couldn't send sales query from the FLUX plugin to Exchange. Report is " + query, e);
        }
    }
}