package io.thorntail.openshift.ts.rolling;

import io.thorntail.openshift.ts.common.arquillian.OpenShiftUtil;
import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
public class RollingIT {
    private static final Logger LOG = Logger.getLogger(RollingIT.class.getName());

    private static final String APP_NAME = System.getProperty("app.name");

    @RouteURL("${app.name}")
    @AwaitRoute
    private String url;

    @ArquillianResource
    private OpenShiftUtil openshift;

    @Test
    public void rollingUpdate() {
        openshift.scale(APP_NAME, 3);

        Counters countersV1 = new Counters();
        await().atMost(3, TimeUnit.MINUTES).untilAsserted(() -> {
            String id =
                    given()
                            .baseUri(url)
                    .when()
                            .get()
                    .then()
                            .statusCode(200)
                            .extract().body().asString();
            countersV1.increment(id);
            assertThat(countersV1.size()).isEqualTo(3);
        });
        LOG.info("Pod IDs v1: " + countersV1);

        assertThat(openshift.countReadyReplicas(APP_NAME)).isEqualTo(3);

        // redeploying triggers rolling update
        openshift.deployLatest(APP_NAME, false);

        Counters countersV2 = new Counters();
        await().atMost(3, TimeUnit.MINUTES).untilAsserted(() -> {
            String id =
                    given()
                            .baseUri(url)
                    .when()
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
        LOG.info("Pod IDs v1 after redeploy: " + countersV1);
        LOG.info("Pod IDs v2: " + countersV2);

        // some pods from V1 might still exist at this point (they are just being deleted)
        assertThat(openshift.countReadyReplicas(APP_NAME)).isGreaterThanOrEqualTo(3);

        // test ends when the 3rd unique ID appears, so one occurrence should have value 1
        // (that would be the first hit which discovered the last pod rolled over from V1 to V2)
        assertThat(countersV2.containsValue(1)).isTrue();
    }

    private static final class Counters {
        private final ConcurrentMap<String, Integer> data = new ConcurrentHashMap<>();

        public void increment(String id) {
            data.merge(id, 1, Integer::sum);
        }

        public boolean containsKey(String id) {
            return data.containsKey(id);
        }

        public boolean containsValue(int value) {
            return data.containsValue(value);
        }

        public int size() {
            return data.size();
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }
}
