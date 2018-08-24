package io.thorntail.openshift.ts.configmap;

import io.thorntail.openshift.ts.common.arquillian.OpenShiftUtil;
import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfigMapIT {
    private static final String APP_NAME = System.getProperty("app.name");

    @ArquillianResource
    private OpenShiftUtil openshift;

    @RouteURL(value = "${app.name}", path = "/api/greeting")
    @AwaitRoute
    private URL greetingUrl;

    @RouteURL(value = "${app.name}", path = "/health")
    private URL healthUrl;

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
        openshift.deployAndRollout(new File("target/test-classes/test-config-update.yml"), APP_NAME, greetingUrl);

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
        openshift.deployAndRollout(new File("target/test-classes/test-config-broken.yml"), APP_NAME, healthUrl);

        given()
                .baseUri(greetingUrl.toString())
        .when()
                .get()
        .then()
                .statusCode(500);
    }

}
