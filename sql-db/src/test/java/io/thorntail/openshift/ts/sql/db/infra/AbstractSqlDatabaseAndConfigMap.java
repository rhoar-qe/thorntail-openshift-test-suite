package io.thorntail.openshift.ts.sql.db.infra;

import io.thorntail.openshift.test.util.OpenShiftUtil;

public abstract class AbstractSqlDatabaseAndConfigMap {
    protected static final String DB_APP_NAME = "test-db";

    protected final OpenShiftUtil openshift;

    protected AbstractSqlDatabaseAndConfigMap(OpenShiftUtil openshift) {
        this.openshift = openshift;
    }

    public abstract void deploy() throws Exception;

    public abstract void undeploy() throws Exception;
}
