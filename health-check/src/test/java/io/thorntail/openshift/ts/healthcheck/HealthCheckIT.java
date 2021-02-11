package io.thorntail.openshift.ts.healthcheck;

import io.thorntail.openshift.test.OpenShiftTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.when;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;

@OpenShiftTest
public class HealthCheckIT {
    @BeforeEach
    public void setUp() {
        await().atMost(5, TimeUnit.MINUTES).untilAsserted(this::simpleInvocation);
    }

    @Test
    public void simpleInvocation() {
        when()
                .get("/api/greeting")
        .then()
                .statusCode(200)
                .body(containsString("Hello, World!"));
    }

    @Test
    public void stopServiceAndWaitForRestart() {
        simpleInvocation();

        when()
                .post("/api/stop")
        .then()
                .statusCode(200);

        await().atMost(5, TimeUnit.MINUTES).untilAsserted(() -> {
            when()
                    .get("/api/greeting")
            .then()
                    .statusCode(503);
        });

        await().atMost(5, TimeUnit.MINUTES).untilAsserted(this::simpleInvocation);
    }
}
