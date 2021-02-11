package io.thorntail.openshift.ts.sql.db.infra;

import io.thorntail.openshift.test.util.OpenShiftUtil;

public class ExternalOracle extends AbstractExternalSqlDatabaseAndConfigMap {
    public ExternalOracle(OpenShiftUtil openshift) {
        super(
                openshift,
                "oracle12c",
                "target/test-classes/project-defaults-external-oracle.yml"
        );
    }
}
