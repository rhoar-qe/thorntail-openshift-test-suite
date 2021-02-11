package io.thorntail.openshift.ts.sql.db.infra;

import io.thorntail.openshift.test.util.OpenShiftUtil;

public class ExternalMysql extends AbstractExternalSqlDatabaseAndConfigMap {
    public ExternalMysql(OpenShiftUtil openshift) {
        super(
                openshift,
                "mysql80&&geo_BOS",
                "target/test-classes/project-defaults-external-mysql.yml"
        );
    }
}
