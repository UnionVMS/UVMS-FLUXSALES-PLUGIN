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
package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesQueryMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.producer.PluginMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import java.util.Date;

@LocalBean
@Stateless
public class ExchangeService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeService.class);

    @EJB
    PluginMessageProducer producer;

    public void sendSalesReportToExchange(Report report) {
        String reportAsString = null;

        try {
            reportAsString = JAXBMarshaller.marshallJaxBObjectToString(report);
            String guid = report.getFLUXSalesReportMessage().getFLUXReportDocument().getIDS().get(0).getValue();
            String countryOfSender = report.getFLUXSalesReportMessage().getFLUXReportDocument().getOwnerFLUXParty().getIDS().get(0).getValue();

            String text = ExchangeModuleRequestMapper.createReceiveSalesReportRequest(reportAsString, guid, countryOfSender,  "FLUX", PluginType.FLUX, new Date());
            producer.sendModuleMessage(text, ModuleQueue.EXCHANGE);

        } catch (ExchangeModelMarshallException e) {
            LOG.error("Couldn't map the sales report in the FLUX Plugin to ReceiveSalesReportRequest.", e);
        } catch (JMSException e) {
            LOG.error("Couldn't send sales report from the FLUX plugin to Exchange. Report is " + reportAsString, e);
        } catch (SalesMarshallException e) {
            LOG.error("Couldn't marshall the supplied sales report", e);
        }
    }
    public void sendSalesResponseToExchange(FLUXSalesResponseMessage response) {
        try {
            String responseAsString = JAXBMarshaller.marshallJaxBObjectToString(response);
            String guid = response.getFLUXResponseDocument().getIDS().get(0).getValue();
            String countryOfSender = response.getFLUXResponseDocument().getRespondentFLUXParty().getIDS().get(0).getValue();

            String text = ExchangeModuleRequestMapper.createReceiveSalesResponseRequest(responseAsString, guid, countryOfSender, new Date(), "FLUX", PluginType.FLUX);
            producer.sendModuleMessage(text, ModuleQueue.EXCHANGE);
        } catch (ExchangeModelMarshallException e) {
            LOG.error("Couldn't map the sales response in the FLUX Plugin to ReceiveSalesResponseRequest. Report is " + response, e);
        } catch (JMSException e) {
            LOG.error("Couldn't send sales response from the FLUX plugin to Exchange. Report is " + response, e);
        } catch (SalesMarshallException e) {
            LOG.error("Couldn't marshall the supplied sales response", e);
        }
    }

    public void sendSalesQueryToExchange(FLUXSalesQueryMessage query) {
        try {
            String queryAsString = JAXBMarshaller.marshallJaxBObjectToString(query);
            String guid = query.getSalesQuery().getID().getValue();
            String countryOfSender = query.getSalesQuery().getSubmitterFLUXParty().getIDS().get(0).getValue();

            String text = ExchangeModuleRequestMapper.createReceiveSalesQueryRequest(queryAsString, guid, countryOfSender, new Date(), "FLUX", PluginType.FLUX);
            producer.sendModuleMessage(text, ModuleQueue.EXCHANGE);
        } catch (ExchangeModelMarshallException e) {
            LOG.error("Couldn't map the sales query in the FLUX Plugin to ReceiveSalesQueryRequest. Report is " + query, e);
        } catch (JMSException e) {
            LOG.error("Couldn't send sales query from the FLUX plugin to Exchange. Report is " + query, e);
        } catch (SalesMarshallException e) {
            LOG.error("Couldn't marshall the supplied sales query", e);
        }
    }
}