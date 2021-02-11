package io.thorntail.openshift.ts.rolling;

import io.thorntail.openshift.test.AppMetadata;
import io.thorntail.openshift.test.OpenShiftTest;
import io.thorntail.openshift.test.injection.TestResource;
import io.thorntail.openshift.test.util.OpenShiftUtil;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.fusesource.jansi.Ansi.ansi;

@OpenShiftTest
public class RollingIT {
    @TestResource
    private OpenShiftUtil openshift;

    @TestResource
    private AppMetadata app;

    @Test
    public void rollingUpdate() {
        openshift.scale(app.name, 3);

        Counters countersV1 = new Counters();
        await().atMost(5, TimeUnit.MINUTES).untilAsserted(() -> {
            String id =
                    when()
                            .get()
                    .then()
                            .statusCode(200)
                            .extract().body().asString();
            countersV1.increment(id);
            assertThat(countersV1.size()).isEqualTo(3);
        });
        System.out.println(ansi().a("Pod IDs ").fgYellow().a("v1").reset().a(": ").a(countersV1));

        assertThat(openshift.countReadyReplicas(app.name)).isEqualTo(3);

        // redeploying triggers rolling update
        openshift.deployLatest(app.name, false);

        Counters countersV2 = new Counters();
        await().atMost(5, TimeUnit.MINUTES).untilAsserted(() -> {
            String id =
                    when()
                            .get()
                    .then()
                            .statusCode(200)
                            .extract().body().asString();
            if (countersV1.containsKey(id)) {
                countersV1.increment(id);
            } else {
                countersV2.increment(id);
            }
            assertThat(countersV2.size()).isEqualTo(3);
        });
        System.out.println(ansi().a("Pod IDs ").fgYellow().a("v1").reset().a(" after redeploy: ").a(countersV1));
        System.out.println(ansi().a("Pod IDs ").fgYellow().a("v2").reset().a(": ").a(countersV2));

        // some pods from V1 might still exist at this point (they are just being deleted)
        assertThat(openshift.countReadyReplicas(app.name)).isGreaterThanOrEqualTo(3);

        // test ends when the 3rd unique ID appears, so one occurrence should have value 1
        // (that would be the first hit which discovered the last pod rolled over from V1 to V2)
        assertThat(countersV2.containsValue(1)).isTrue();
    }
}
