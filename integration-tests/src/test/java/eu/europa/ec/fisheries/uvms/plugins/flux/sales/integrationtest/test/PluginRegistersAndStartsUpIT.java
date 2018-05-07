package eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.test;

import eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.deployment.TestOnGoodWorkingPlugin;
import eu.europa.ec.fisheries.uvms.plugins.flux.sales.service.StartupBean;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
public class PluginRegistersAndStartsUpIT extends TestOnGoodWorkingPlugin {

	@EJB
	private StartupBean startupBean;

	@Test
	@OperateOnDeployment("good-working-plugin")
	public void pluginRegistersAndStartsUpSuccessfully() throws Exception {
        await().atMost(30, SECONDS)
                .until(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return startupBean.isRegistered();
                    }
                });

        await().atMost(30, SECONDS)
                .until(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return startupBean.isEnabled();
            }
        });
	}
}
