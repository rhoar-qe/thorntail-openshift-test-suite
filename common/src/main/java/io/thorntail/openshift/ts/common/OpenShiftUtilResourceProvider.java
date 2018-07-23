package io.thorntail.openshift.ts.common;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

import java.lang.annotation.Annotation;

public class OpenShiftUtilResourceProvider implements ResourceProvider {
    @Inject
    private Instance<OpenShiftUtil> openShiftUtilInstance;

    @Override
    public boolean canProvide(Class<?> type) {
        return OpenShiftUtil.class.equals(type);
    }

    @Override
    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        OpenShiftUtil openShiftUtil = openShiftUtilInstance.get();

        if (openShiftUtil == null) {
            throw new IllegalStateException("Unable to inject OpenShiftUtil.");
        }

        return openShiftUtil;
    }
}
