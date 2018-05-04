package eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.deployment;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

public class DeploymentFactory {

    private DeploymentFactory() {
        // hide public constructor
    }


    public static WebArchive createStandardDeployment() {
        WebArchive archive = createArchiveWithTestFiles("test");
        archive.addPackages(true, "eu.europa.ec.fisheries.uvms.commons.message");
        archive.addPackages(true, "eu.europa.ec.fisheries.uvms.plugins.flux.sales.model");
        archive.addPackages(true, "eu.europa.ec.fisheries.schema.flux.sales");
        archive.addPackages(true, "xeu.bridge_connector");
        archive.addPackages(true, "xeu.connector_bridge");
        archive.addPackages(true, "eu.europa.ec.fisheries.uvms.plugins.flux.sales.service");
        archive.addPackages(true, "eu.europa.ec.fisheries.uvms.plugins.flux.sales.webservice");

        archive.addAsResource("capabilities.properties", "capabilities.properties");
        archive.addAsResource("plugin.properties", "plugin.properties");
        archive.addAsResource("settings.properties", "settings.properties");
        archive.addAsResource("flux.properties", "flux.properties");
        archive.addAsResource("sales/codelist_standard_UNECE_CommunicationMeansTypeCode_D16A.xsd");
        archive.addAsResource("sales/FLUXSalesQueryMessage_3p0.xsd");
        archive.addAsResource("sales/FLUXSalesReportMessage_3p0.xsd");
        archive.addAsResource("sales/FLUXSalesResponseMessage_3p0.xsd");
        archive.addAsResource("sales/QualifiedDataType_20p0.xsd");
        archive.addAsResource("sales/ReusableAggregateBusinessInformationEntity_20p0.xsd");
        archive.addAsResource("sales/UnqualifiedDataType_20p0.xsd");

        return archive;
    }

    private static WebArchive createArchiveWithTestFiles(final String name) {
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeDependencies().resolve().withTransitivity().asFile();

        // Embedding war package which contains the test class is needed
        // So that Arquillian can invoke test class through its servlet test runner
        WebArchive testWar = ShrinkWrap.create(WebArchive.class, name + ".war");

        testWar.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        testWar.addAsWebInfResource("ejb-jar-test.xml", "ejb-jar.xml");
        testWar.addAsResource("logback-test.xml", "logback.xml");
        testWar.addAsManifestResource("jboss-deployment-structure.xml","jboss-deployment-structure.xml");

        testWar.addAsLibraries(files);

        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.test");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.deployment");
        testWar.addPackages(true, "org.awaitility");
        testWar.addClass("eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.mock.PortInitiatorMock");
        testWar.addClass("eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.mock.PortMock");

        return testWar;
    }

}
