package io.thorntail.openshift.ts.common.arquillian;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.fabric8.kubernetes.api.model.v4_10.Pod;
import io.fabric8.kubernetes.clnt.v4_10.internal.readiness.Readiness;
import io.fabric8.openshift.clnt.v4_10.OpenShiftClient;
import org.arquillian.cube.openshift.impl.client.ResourceUtil;

import static org.awaitility.Awaitility.await;

public final class OpenShiftUtil {
    private final OpenShiftClient oc;

    OpenShiftUtil(OpenShiftClient oc) {
        this.oc = oc;
    }

    private List<Pod> listPodsForDeploymentConfig(String deploymentConfigName) {
        return oc.pods()
                .inNamespace(oc.getNamespace())
                .withLabel("deploymentconfig", deploymentConfigName)
                .list()
                .getItems();
    }

    public void scale(String deploymentConfigName, int replicas) {
        oc.deploymentConfigs()
                .inNamespace(oc.getNamespace())
                .withName(deploymentConfigName)
                .scale(replicas);

        awaitDeploymentReadiness(deploymentConfigName, replicas);
    }

    public void deployLatest(String deploymentConfigName, boolean waitForAllReplicas) {
        oc.deploymentConfigs()
                .inNamespace(oc.getNamespace())
                .withName(deploymentConfigName)
                .deployLatest(waitForAllReplicas);
    }

    public void awaitDeploymentReadiness(String deploymentConfigName, int expectedReplicas) {
        await().atMost(5, TimeUnit.MINUTES).until(() -> {
            // ideally, we'd look at deployment config's status.availableReplicas field,
            // but that's only available since OpenShift 3.5
            List<Pod> pods = listPodsForDeploymentConfig(deploymentConfigName);
            try {
                return pods.size() == expectedReplicas && pods.stream().allMatch(Readiness::isPodReady);
            } catch (IllegalStateException e) {
                // the 'Ready' condition can be missing sometimes, in which case Readiness.isPodReady throws an exception
                // here, we'll swallow that exception in hope that the 'Ready' condition will appear later
                return false;
            }
        });
    }

    public int countReadyReplicas(String deploymentConfigName) {
        List<Pod> pods = listPodsForDeploymentConfig(deploymentConfigName);

        int number = 0;
        for (Pod pod : pods) {
            if (Readiness.isPodReady(pod)) {
                number++;
            }
        }
        return number;
    }

    public void rolloutChanges(String deploymentConfigName, URL awaitUrl) {
        int replicas = countReadyReplicas(deploymentConfigName);

        // in reality, user would do `oc rollout latest`, but that's hard (racy) to wait for
        // so here, we'll scale down to 0, wait for that, then scale back to original number of replicas and wait again
        scale(deploymentConfigName, 0);
        scale(deploymentConfigName, replicas);

        ResourceUtil.awaitRoute(awaitUrl, 200);
    }

    public void applyYaml(File yaml) throws IOException {
        try (InputStream is = new FileInputStream(yaml)) {
            applyYaml(is);
        }
    }

    public void applyYaml(InputStream yaml) {
        oc.load(yaml).createOrReplace();
    }

    public void deleteYaml(File yaml) throws IOException {
        try (InputStream is = new FileInputStream(yaml)) {
            deleteYaml(is);
        }
    }

    public void deleteYaml(InputStream yaml) {
        oc.load(yaml).delete();
    }
}
