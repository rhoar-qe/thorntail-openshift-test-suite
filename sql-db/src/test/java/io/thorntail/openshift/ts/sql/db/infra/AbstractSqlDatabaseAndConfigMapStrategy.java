package io.thorntail.openshift.ts.sql.db.infra;

import io.fabric8.kubernetes.clnt.v4_10.KubernetesClient;
import io.fabric8.openshift.clnt.v4_10.OpenShiftClient;
import io.thorntail.openshift.ts.common.arquillian.OpenShiftUtil;
import io.thorntail.openshift.ts.sql.db.arquillian.SqlDatabaseAndConfigMapStrategy;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;

public abstract class AbstractSqlDatabaseAndConfigMapStrategy implements SqlDatabaseAndConfigMapStrategy {
    protected static final String DB_APP_NAME = "test-db";

    @Inject
    private Instance<KubernetesClient> kubernetesClient;

    @Inject
    private Instance<OpenShiftUtil> openShiftUtil;

    protected final OpenShiftClient oc() {
        return kubernetesClient.get().adapt(OpenShiftClient.class);
    }

    protected final OpenShiftUtil openshift() {
        return openShiftUtil.get();
    }
}
