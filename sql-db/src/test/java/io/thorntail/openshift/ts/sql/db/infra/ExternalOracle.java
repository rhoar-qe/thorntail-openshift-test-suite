package io.thorntail.openshift.ts.sql.db.infra;

public class ExternalOracle extends AbstractExternalSqlDatabaseAndConfigMapStrategy {
    public ExternalOracle() {
        super("oracle12c", "target/test-classes/project-defaults-external-oracle.yml");
    }
}
