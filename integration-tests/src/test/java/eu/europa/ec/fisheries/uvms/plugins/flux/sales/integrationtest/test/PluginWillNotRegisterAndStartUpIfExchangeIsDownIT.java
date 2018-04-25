package eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.test;

import eu.europa.ec.fisheries.uvms.plugins.flux.sales.integrationtest.deployment.TestOnPluginWithDefectEventBus;
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

@Ignore
@RunWith(Arquillian.class)
public class PluginWillNotRegisterAndStartUpIfExchangeIsDownIT extends TestOnPluginWithDefectEventBus {

	@EJB
	private StartupBean startupBean;

	@Test
	@OperateOnDeployment("plugin-with-defect-event-bus")
	public void pluginWillNotRegisterAndStartUpIfExchangeIsDown() throws Exception {
        await().pollDelay(30, SECONDS)
				.timeout(40, SECONDS)
                .until(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return !startupBean.isRegistered() && !startupBean.isEnabled() ;
                    }
                });
	}
}
