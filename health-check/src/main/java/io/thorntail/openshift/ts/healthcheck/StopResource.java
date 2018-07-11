package io.thorntail.openshift.ts.healthcheck;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/stop")
public class StopResource {
    /**
     * The /stop operation is actually just going to suspend the server inbound traffic,
     * which leads to 503 when subsequent HTTP requests are received
     */
    @POST
    public Response stop() {
        ModelNode op = new ModelNode();
        op.get("address").setEmptyList();
        op.get("operation").set("suspend");

        try (ModelControllerClient client = ModelControllerClient.Factory.create("localhost", 9990)) {
            ModelNode response = client.execute(op);

            if (response.has("failure-description")) {
                throw new RuntimeException(response.get("failure-description").asString());
            }

            return Response.ok(response.get("result").asString()).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
