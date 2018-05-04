package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;

@Singleton
public class DefaultOrikaMapper {

    private MapperFacade mapperFacade;

    @PostConstruct
    public void init() {
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .mapNulls(false)
                .build();

        ConverterFactory converterFactory = factory.getConverterFactory();
        converterFactory.registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));

        mapperFacade = factory.getMapperFacade();
    }

    @Produces
    public MapperFacade getMapper() {
        return mapperFacade;
    }


}
