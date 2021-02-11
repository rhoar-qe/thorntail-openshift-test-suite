package io.thorntail.openshift.ts.sql.db.infra;

import io.thorntail.openshift.test.util.OpenShiftUtil;

public class ExternalPostgresql extends AbstractExternalSqlDatabaseAndConfigMap {
    public ExternalPostgresql(OpenShiftUtil openshift) {
        super(
                openshift,
                "postgresql115&&geo_RDU",
                "target/test-classes/project-defaults-external-postgresql.yml"
        );
    }
}
