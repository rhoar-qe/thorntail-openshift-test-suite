package io.thorntail.openshift.ts.sql.db.infra;

import io.thorntail.openshift.test.Command;

import java.io.IOException;

public final class ProjectCleanup {
    /**
     * The current OpenShift project will be cleaned up.
     * Not all resources are deleted, only resources of these types:
     *
     * <ul>
     *     <li>Deployment</li>
     *     <li>DeploymentConfig</li>
     *     <li>ReplicaSet</li>
     *     <li>ReplicationController</li>
     *     <li>Pod</li>
     *     <li>Service</li>
     *     <li>Route</li>
     *     <li>Template</li>
     *     <li>ConfigMap</li>
     * </ul>
     *
     * Resources related to images (such as BuildConfig or ImageStream) are specifically not deleted, because
     * they are expected to be used for multiple tests.
     */
    public void run() throws IOException, InterruptedException {
        new Command("oc", "delete",
                "deployment,deploymentconfig,replicaset,replicationcontroller,pod,service,route,template,configmap",
                "--all").runAndWait();
    }
}
