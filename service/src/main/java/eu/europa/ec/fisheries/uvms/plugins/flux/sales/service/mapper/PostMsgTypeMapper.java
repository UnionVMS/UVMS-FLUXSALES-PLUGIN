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
package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.mapper;

import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.exception.MappingException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xeu.connector_bridge.v1.POSTMSG;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import java.util.*;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkNotNull;

@LocalBean
@Stateless
@Slf4j
public class PostMsgTypeMapper {

    @EJB
    private StartupBean startupBean;


    public POSTMSG wrapInPostMsgType(@NotNull Object toBeWrapped, @NotNull String df, @NotNull String country) throws MappingException {
        validateInputs(toBeWrapped, df, country);
        String ad = convertCountryToFluxNode(country);

        POSTMSG message = new POSTMSG();
        message.setBUSINESSUUID(UUID.randomUUID().toString());
        message.setAD(ad);
        message.setDF(df);
        message.setDT(DateTime.now().withZone(DateTimeZone.UTC));
        message.setAny(marshalToDOM(toBeWrapped));

        return message;
    }

    private String convertCountryToFluxNode(String country) {
        String[] fluxNodes = startupBean.getSetting("flux_nodes").split(",");
        return Arrays.stream(fluxNodes)
                .map(fluxNode -> fluxNode.toUpperCase())
                .filter(fluxNode -> fluxNode.startsWith(country))
                .findFirst()
                .orElse(country);
    }

    private void validateInputs(Object toBeWrapped, String df, String ad) {
        checkNotNull(df, "DF can't be null");
        checkNotNull(ad, "AD can't be null");
        checkNotNull(toBeWrapped, "Object to be wrapped can't be null");
    }

    private Element marshalToDOM(Object toBeWrapped) throws MappingException {
        try {
            JAXBContext context = JAXBContext.newInstance(toBeWrapped.getClass());
            Marshaller marshaller = context.createMarshaller();
            DOMResult domResult = new DOMResult();
            marshaller.marshal(toBeWrapped, domResult);

            return ((Document) domResult.getNode()).getDocumentElement();
        } catch (JAXBException e) {
            throw new MappingException("Could not wrap object " + toBeWrapped + " in post msg", e);
        }
    }

    public POSTMSG mapToResponse(Object message, String id, String df, String ad) throws JAXBException {
        POSTMSG postMsgType = new POSTMSG();
        postMsgType.setBUSINESSUUID(id);
        postMsgType.setAD(ad);
        postMsgType.setDF(df);

        JAXBContext context = JAXBContext.newInstance(message.getClass());
        Marshaller marshaller = context.createMarshaller();
        DOMResult domResult = new DOMResult();
        marshaller.marshal(message, domResult);

        Element elt = ((Document) domResult.getNode()).getDocumentElement();

        postMsgType.setAny(elt);

        return postMsgType;
    }

    public void addHeaderValueToRequest(Object port, final Map<String, String> values) {
        BindingProvider bp = (BindingProvider) port;
        Map<String, Object> context = bp.getRequestContext();

        Map<String, List<String>> headers = new HashMap<>();
        for (Entry entry : values.entrySet()) {
            headers.put(entry.getKey().toString(), Collections.singletonList(entry.getValue().toString()));
        }
        context.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
    }
}
