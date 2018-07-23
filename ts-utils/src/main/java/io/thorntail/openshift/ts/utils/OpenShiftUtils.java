/*
 *
 *  Copyright 2018 Red Hat, Inc, and individual contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.thorntail.openshift.ts.utils;

import static org.awaitility.Awaitility.await;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.arquillian.cube.openshift.impl.client.OpenShiftAssistant;

import io.fabric8.kubernetes.api.model.v3_1.Pod;
import io.fabric8.kubernetes.api.model.v3_1.PodList;
import io.fabric8.kubernetes.clnt.v3_1.internal.readiness.Readiness;
import io.fabric8.openshift.clnt.v3_1.OpenShiftClient;

public abstract class OpenShiftUtils {

    public static void awaitAppDeployed (OpenShiftAssistant openShiftAssistant, String appName) {
        OpenShiftClient oc = openShiftAssistant.getClient();
        await().atMost(5, TimeUnit.MINUTES).until(() -> {
            Pod dbPod = null;

            while (dbPod == null) {
                PodList pods = oc
                .pods()
                .inNamespace(oc.getNamespace())
                .list();

                for (Pod pod : pods.getItems()) {
                    String podKind = pod.getKind();
                    String podName = pod.getMetadata().getName();
                    if (podName.startsWith(appName) && "Pod".equals(podKind)) {
                        dbPod = pod;
                        break;
                    }
                }
            }

            try {
                return Readiness.isPodReady(dbPod);
            } catch (IllegalStateException e) {
                // the 'Ready' condition can be missing sometimes, in which case Readiness.isPodReady throws an exception
                // here, we'll swallow that exception in hope that the 'Ready' condition will appear later
                return false;
            }
       });
    }

    public static void deployConfigMapAndRollout(OpenShiftAssistant openShiftAssistant, String path, String appName, URL healthURL) throws Exception {
        try (InputStream yaml = new FileInputStream(path)) {
            deployConfigMapAndRollout(openShiftAssistant, yaml, appName, healthURL);
        }
    }

    public static void deployConfigMapAndRollout(OpenShiftAssistant openShiftAssistant, InputStream yaml, String appName, URL healthURL) throws Exception {
        openShiftAssistant.deploy(yaml);

        rolloutChanges(openShiftAssistant, appName, healthURL);
    }

    public static void rolloutChanges(OpenShiftAssistant openShiftAssistant, String appName, URL healthURL) throws InterruptedException {
        System.out.println("Rollout changes to " + appName);

        // in reality, user would do `oc rollout latest`, but that's hard (racy) to wait for
        // so here, we'll scale down to 0, wait for that, then scale back to 1 and wait again
        openShiftAssistant.scale(appName, 0);
        openShiftAssistant.awaitUrl(healthURL, 500, 503);
        openShiftAssistant.scale(appName, 1);
        openShiftAssistant.awaitUrl(healthURL, 200);
    }
}
