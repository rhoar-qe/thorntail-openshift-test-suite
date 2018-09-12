package io.thorntail.openshift.ts.topology1;

import org.wildfly.swarm.topology.Topology;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

@Path("/topology1")
@ApplicationScoped
public class Topology1Resource {
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
