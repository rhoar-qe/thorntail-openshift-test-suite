package io.thorntail.openshift.ts.common;

import io.fabric8.kubernetes.api.model.v4_0.Pod;
import io.fabric8.kubernetes.clnt.v4_0.internal.readiness.Readiness;
import io.fabric8.openshift.clnt.v4_0.OpenShiftClient;
import org.arquillian.cube.openshift.impl.client.ResourceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public final class OpenShiftUtil {
    private final OpenShiftClient oc;

    OpenShiftUtil(OpenShiftClient oc) {
        this.oc = oc;
    }

    public void scale(String name, int replicas) {
        oc.deploymentConfigs()
                .inNamespace(oc.getNamespace())
                .withName(name)
                .scale(replicas);

        awaitDeploymentReadiness(name, replicas);
    }

    public void awaitDeploymentReadiness(String deploymentConfigName, int expectedReplicas) {
        await().atMost(5, TimeUnit.MINUTES).until(() -> {
            // ideally, we'd look at deployment config's status.availableReplicas field,
            // but that's only available since OpenShift 3.5
            List<Pod> pods = oc
                    .pods()
                    .inNamespace(oc.getNamespace())
                    .withLabel("deploymentconfig", deploymentConfigName)
                    .list()
                    .getItems();
            try {
                return pods.size() == expectedReplicas && pods.stream().allMatch(Readiness::isPodReady);
            } catch (IllegalStateException e) {
                // the 'Ready' condition can be missing sometimes, in which case Readiness.isPodReady throws an exception
                // here, we'll swallow that exception in hope that the 'Ready' condition will appear later
                return false;
            }
        });
    }

    public void rolloutChanges(String appName, URL healthUrl) {
        // in reality, user would do `oc rollout latest`, but that's hard (racy) to wait for
        // so here, we'll scale down to 0, wait for that, then scale back to 1 and wait again
        scale(appName, 0);
        scale(appName, 1);

        ResourceUtil.awaitRoute(healthUrl, 200);
    }

    public void deployAndRollout(File yaml, String appName, URL healthUrl) throws IOException {
        try (InputStream is = new FileInputStream(yaml)) {
            deployAndRollout(is, appName, healthUrl);
        }
    }

    public void deployAndRollout(InputStream yaml, String appName, URL healthUrl) {
        oc.load(yaml).createOrReplace();

        rolloutChanges(appName, healthUrl);
    }
}
