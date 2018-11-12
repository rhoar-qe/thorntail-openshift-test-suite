package io.thorntail.openshift.ts.scaling;

import io.restassured.response.ValidatableResponse;
import io.thorntail.openshift.ts.common.arquillian.OpenShiftUtil;
import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.awaitility.core.ThrowingRunnable;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
public class ScalingIT {
    private static final String APP_NAME = System.getProperty("app.name");

    @RouteURL(value = "${app.name}")
    @AwaitRoute
    private String url;

    @ArquillianResource
    private OpenShiftUtil openshift;

    @After
    public void scaleBackToOne() {
        openshift.scale(APP_NAME, 1);
    }

    @Test
    public void scaleUp() {
        openshift.scale(APP_NAME, 2);

        Set<String> uniqueIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
        await().atMost(1, TimeUnit.MINUTES).untilAsserted(() -> {
            String uniqueId =
                    given()
                            .baseUri(url)
                    .when()
                            .get()
                    .then()
                            .statusCode(200)
                            .extract().body().asString();
            uniqueIds.add(uniqueId);

            assertThat(uniqueIds).hasSize(2);
        });

        assertThat(openshift.countReadyReplicas(APP_NAME)).isEqualTo(2);
    }

    @Test
    public void scaleDown() {
        scaleUp();

        openshift.scale(APP_NAME, 1);

        assertSingleReplica();
    }

    @Test
    public void scaleDownToZero() {
        assertSingleReplica();

        openshift.scale(APP_NAME, 0);

        for (int i = 0; i < 100; i++) {
            given()
                    .baseUri(url)
            .when()
                    .get()
            .then()
                    .statusCode(503);
        }
    }

    private void assertSingleReplica() {
        Set<String> uniqueIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
        for (int i = 0; i < 100; i++) {
            String uniqueId =
                    given()
                            .baseUri(url)
                    .when()
                            .get()
                    .then()
                            .statusCode(200)
                            .extract().body().asString();
            uniqueIds.add(uniqueId);
        }

        assertThat(uniqueIds).hasSize(1);

        assertThat(openshift.countReadyReplicas(APP_NAME)).isEqualTo(1);
    }
}
