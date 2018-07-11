package io.thorntail.openshift.ts.configmap;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/greeting")
@ApplicationScoped
public class GreetingResource {
    @Inject
    @ConfigurationValue("greeting.message")
    private Optional<String> message;

    @GET
    @Produces("application/json")
    public Response greeting(@QueryParam("name") @DefaultValue("World") String name) {
        if (message.isPresent()) {
            return Response.ok().entity(new Greeting(String.format(message.get(), name))).build();
        }

        return Response.status(500).entity("ConfigMap not present").build();
    }
}
