package io.thorntail.openshift.ts.common;

import org.arquillian.cube.openshift.impl.client.OpenShiftClient;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

public class OpenShiftUtilProducer {
    @Inject
    @ApplicationScoped
    private InstanceProducer<OpenShiftUtil> openShiftUtilInstanceProducer;

    public void createOpenShiftAssistant(@Observes OpenShiftClient openShiftClient) {
        OpenShiftUtil openShiftUtil = new OpenShiftUtil(openShiftClient.getClientExt());
        openShiftUtilInstanceProducer.set(openShiftUtil);
    }
}
