package io.thorntail.openshift.ts.common.arquillian;

import org.arquillian.cube.kubernetes.impl.utils.CommandExecutor;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;

import java.util.logging.Logger;

public class ProjectCleanupObserver {
    private static final Logger log = Logger.getLogger(ProjectCleanupObserver.class.getName());

    public void cleanup(@Observes(precedence = 10_000) BeforeClass event) {
        if (event.getTestClass().isAnnotationPresent(ProjectCleanup.class)) {
            log.info("Cleaning current OpenShift project");
            CommandExecutor cmd = new CommandExecutor();
            cmd.execCommand("oc", "delete", "deployment,deploymentconfig,replicaset,replicationcontroller,pod,service,route,template,configmap", "--all");
        }
    }
}
