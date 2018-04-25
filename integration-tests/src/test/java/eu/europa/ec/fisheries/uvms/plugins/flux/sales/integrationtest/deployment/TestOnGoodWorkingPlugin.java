package eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.deployment;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public abstract class TestOnGoodWorkingPlugin {

    @Deployment(name = "good-working-plugin", order = 1)
    public static Archive<?> createFluxActivityWSPluginDeployment() {
        WebArchive archive = DeploymentFactory.createStandardDeployment();
        archive.addClass("eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.mock.EventBusConsumerMock");
        archive.addClass("eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.mock.ExchangeConsumerMock");
        return archive;
    }

}
