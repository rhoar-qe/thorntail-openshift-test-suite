package io.thorntail.openshift.ts.configmap;

import org.arquillian.cube.openshift.impl.client.OpenShiftAssistant;
import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfigMapIT {
    private static final String APP_NAME = System.getProperty("app.name");

    @ArquillianResource
    private OpenShiftAssistant openShiftAssistant;

    @RouteURL(value = "${app.name}", path = "/api/greeting")
    @AwaitRoute
    private URL greetingUrl;

    @RouteURL(value = "${app.name}", path = "/health")
    private String healthUrl;

    @Test
    public void _1_simpleInvocation() {
        given()
                .baseUri(greetingUrl.toString())
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello World from a ConfigMap!"));
    }

    @Test
    public void _2_invocationWithParam() {
        given()
                .baseUri(greetingUrl.toString())
                .queryParam("name", "Steve")
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello Steve from a ConfigMap!"));
    }

    @Test
    public void _3_updateConfigMap() throws Exception {
        deployConfigMap("target/test-classes/test-config-update.yml");

        rolloutChanges();

        given()
                .baseUri(greetingUrl.toString())
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Good morning World from an updated ConfigMap!"));
    }

    @Test
    public void _4_wrongConfiguration() throws Exception {
        deployConfigMap("target/test-classes/test-config-broken.yml");

        rolloutChanges();

        given()
                .baseUri(greetingUrl.toString())
        .when()
                .get()
        .then()
                .statusCode(500);
    }

    private void deployConfigMap(String path) throws IOException {
        try (InputStream yaml = new FileInputStream(path)) {
            // in this test, this always replaces an existing configmap, which is already tracked for deleting
            // after the test finishes
            openShiftAssistant.deploy(yaml);
        }
    }

    private void rolloutChanges() {
        // in reality, user would do `oc rollout latest`, but that's hard (racy) to wait for
        // so here, we'll scale down to 0, wait for that, then scale back to 1 and wait again
        openShiftAssistant.scale(APP_NAME, 0);

        await().atMost(5, TimeUnit.MINUTES).untilAsserted(() -> {
            given()
                    .baseUri(healthUrl)
            .when()
                    .get()
            .then()
                    .statusCode(503);
        });

        openShiftAssistant.scale(APP_NAME, 1);

        await().atMost(5, TimeUnit.MINUTES).untilAsserted(() -> {
            given()
                    .baseUri(healthUrl)
            .when()
                    .get()
            .then()
                    .statusCode(200);
        });
    }
}
