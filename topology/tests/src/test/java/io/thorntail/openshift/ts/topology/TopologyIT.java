package io.thorntail.openshift.ts.topology;

import io.thorntail.openshift.test.OpenShiftTest;
import io.thorntail.openshift.test.injection.TestResource;
import io.thorntail.openshift.test.injection.WithName;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@OpenShiftTest
public class TopologyIT {
    @TestResource
    @WithName("topology-1")
    private URL urlTopology1;

    @TestResource
    @WithName("topology-2")
    private URL urlTopology2;

    @Test
    public void topology1() {
        given()
                .baseUri(urlTopology1.toString())
        .when()
                .get("/api/topology1")
        .then()
                .statusCode(200)
                .body(containsString("topology-1"))
                .body(containsString("topology-2"));
    }

    @Test
    public void topology2() {
        given()
                .baseUri(urlTopology2.toString())
        .when()
                .get("/api/topology2")
        .then()
                .statusCode(200)
                .body(containsString("topology-1"))
                .body(containsString("topology-2"));
    }
}
