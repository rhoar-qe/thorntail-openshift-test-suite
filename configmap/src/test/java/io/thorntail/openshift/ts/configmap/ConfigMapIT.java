package io.thorntail.openshift.ts.configmap;

import io.restassured.RestAssured;
import io.thorntail.openshift.test.AppMetadata;
import io.thorntail.openshift.test.OpenShiftTest;
import io.thorntail.openshift.test.injection.TestResource;
import io.thorntail.openshift.test.util.OpenShiftUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;

@OpenShiftTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConfigMapIT {
    @TestResource
    private OpenShiftUtil openshift;

    @TestResource
    private AppMetadata app;

    @BeforeEach
    public void setUp() {
        RestAssured.basePath = "/api/greeting";
    }

    @Test
    @Order(1)
    public void simpleInvocation() {
        when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello World from a ConfigMap!"));
    }

    @Test
    @Order(2)
    public void invocationWithParam() {
        given()
                .queryParam("name", "Steve")
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello Steve from a ConfigMap!"));
    }

    @Test
    @Order(3)
    public void updateConfigMap() throws Exception {
        openshift.applyYaml(new File("target/test-classes/test-config-update.yml"));
        openshift.rolloutChanges(app.name);

        when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Good morning World from an updated ConfigMap!"));
    }

    @Test
    @Order(4)
    public void wrongConfiguration() throws Exception {
        openshift.applyYaml(new File("target/test-classes/test-config-broken.yml"));
        openshift.rolloutChanges(app.name, false);

        await().atMost(5, TimeUnit.MINUTES).untilAsserted(() -> {
            when()
                    .get()
            .then()
                    .statusCode(500);
        });
    }
}
