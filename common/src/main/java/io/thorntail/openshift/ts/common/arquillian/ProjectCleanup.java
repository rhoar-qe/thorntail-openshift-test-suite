package io.thorntail.openshift.ts.common.arquillian;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If a test class is annotated with {@code @ProjectCleanup}, the current OpenShift project will be cleaned up
 * before the test class is executed. Not all resources are deleted, only resources of these types:
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
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProjectCleanup {
}
