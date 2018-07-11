package io.thorntail.openshift.ts.healthcheck;

import org.arquillian.cube.openshift.impl.client.OpenShiftAssistant;
import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;

@RunWith(Arquillian.class)
public class HealthCheckIT {
    @RouteURL(value = "${app.name}", path = "/api/greeting")
    @AwaitRoute
    private URL greetingUrl;

    @RouteURL(value = "${app.name}", path = "/api/stop")
    private String stopUrl;

    @ArquillianResource
    private OpenShiftAssistant openShiftAssistant;

    @Test
    public void simpleInvocation() {
        given()
                .baseUri(greetingUrl.toString())
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello, World!"));
    }

    @Test
    public void stopServiceAndWaitForRestart() {
        simpleInvocation();

        given()
                .baseUri(stopUrl)
        .when()
                .post()
        .then()
                .statusCode(200);

        await().atMost(5, TimeUnit.MINUTES).untilAsserted(() -> {
            given()
                    .baseUri(greetingUrl.toString())
            .when()
                    .get()
            .then()
                    .statusCode(503);
        });

        await().atMost(5, TimeUnit.MINUTES).untilAsserted(this::simpleInvocation);
    }
}
