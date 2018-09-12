package io.thorntail.openshift.ts.topology2;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.wildfly.swarm.topology.Topology;

import java.util.List;
import java.util.Map;

@Path("/topology2")
@ApplicationScoped
public class Topology2Resource {
    @GET
    public String get() throws NamingException {
        StringBuilder response = new StringBuilder();

        Topology topology = Topology.lookup();
        Map<String, List<Topology.Entry>> entries = topology.asMap();
        for (String key : entries.keySet()) {
            response.append(key).append("\n");
        }

        return response.toString();
    }
}
