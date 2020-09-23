package io.thorntail.openshift.ts.topology;

import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@RunWith(Arquillian.class)
public class TopologyIT {
    @RouteURL(value = "topology-1", path = "/api/topology1")
    @AwaitRoute(path = "/health", repetitions = 10, timeout = 10)
    private String urlTopology1;

    @RouteURL(value = "topology-2", path = "/api/topology2")
    @AwaitRoute(path = "/health", repetitions = 10, timeout = 10)
    private String urlTopology2;

    @Test
    public void topology1() {
        given()
                .baseUri(urlTopology1)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("topology-1"))
                .body(containsString("topology-2"));
    }

    @Test
    public void topology2() {
        given()
                .baseUri(urlTopology2)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("topology-1"))
                .body(containsString("topology-2"));
    }
}
