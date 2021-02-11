package io.thorntail.openshift.ts.scaling;

import io.thorntail.openshift.test.AppMetadata;
import io.thorntail.openshift.test.OpenShiftTest;
import io.thorntail.openshift.test.injection.TestResource;
import io.thorntail.openshift.test.util.OpenShiftUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@OpenShiftTest
public class ScalingIT {
    @TestResource
    private OpenShiftUtil openshift;

    @TestResource
    private AppMetadata app;

    @TestResource
    private URL url;

    @AfterEach
    public void scaleBackToOne() {
        openshift.scale(app.name, 1);
    }

    @Test
    public void scaleUp() {
        openshift.scale(app.name, 2);

        Set<String> uniqueIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
        await().atMost(5, TimeUnit.MINUTES).untilAsserted(() -> {
            String uniqueId =
                    when()
                            .get()
                    .then()
                            .statusCode(200)
                            .extract().body().asString();
            uniqueIds.add(uniqueId);

            assertThat(uniqueIds).hasSize(2);
        });

        assertThat(openshift.countReadyReplicas(app.name)).isEqualTo(2);
    }

    @Test
    public void scaleDown() {
        scaleUp();

        openshift.scale(app.name, 1);

        assertSingleReplica();
    }

    @Test
    public void scaleDownToZero() {
        assertSingleReplica();

        openshift.scale(app.name, 0);

        for (int i = 0; i < 100; i++) {
            when()
                    .get()
            .then()
                    .statusCode(503);
        }
    }

    private void assertSingleReplica() {
        Set<String> uniqueIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
        for (int i = 0; i < 100; i++) {
            String uniqueId =
                    when()
                            .get()
                    .then()
                            .statusCode(200)
                            .extract().body().asString();
            uniqueIds.add(uniqueId);
        }

        assertThat(uniqueIds).hasSize(1);

        assertThat(openshift.countReadyReplicas(app.name)).isEqualTo(1);
    }
}
