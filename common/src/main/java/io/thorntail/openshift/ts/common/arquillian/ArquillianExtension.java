package io.thorntail.openshift.ts.common.arquillian;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class ArquillianExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(OpenShiftUtilProducer.class);
        builder.service(ResourceProvider.class, OpenShiftUtilResourceProvider.class);
    }
}
