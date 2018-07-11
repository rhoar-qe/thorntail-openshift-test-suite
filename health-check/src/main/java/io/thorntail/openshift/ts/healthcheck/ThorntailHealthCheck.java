package io.thorntail.openshift.ts.healthcheck;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;

@Health
@ApplicationScoped
public class ThorntailHealthCheck implements HealthCheck {
    public HealthCheckResponse call() {
        ModelNode op = new ModelNode();
        op.get("address").setEmptyList();
        op.get("operation").set("read-attribute");
        op.get("name").set("suspend-state");

        try (ModelControllerClient client = ModelControllerClient.Factory.create("localhost", 9990)) {
            ModelNode response = client.execute(op);

            if (response.has("failure-description")) {
                throw new RuntimeException(response.get("failure-description").asString());
            }

            boolean isRunning = response.get("result").asString().equals("RUNNING");
            if (isRunning) {
                return HealthCheckResponse.named("server-state").up().build();
            } else {
                return HealthCheckResponse.named("server-state").down().build();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
