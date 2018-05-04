package eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.mapper;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DefaultOrikaMapperTest {

    @Test
    public void testCreateMapper() {
        DefaultOrikaMapper defaultOrikaMapper = new DefaultOrikaMapper();
        defaultOrikaMapper.init();
        assertNotNull(defaultOrikaMapper.getMapper());
    }

}