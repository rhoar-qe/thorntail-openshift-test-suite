package io.thorntail.openshift.ts.http;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/greeting")
public class GreetingResource {
    private static final String TEMPLATE = "Hello, %s!";

    @GET
    @Produces("application/json")
    public Greeting greeting(@QueryParam("name") @DefaultValue("World") String name) {
        return new Greeting(String.format(TEMPLATE, name));
    }
}
