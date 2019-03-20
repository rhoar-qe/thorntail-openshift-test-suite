package io.thorntail.openshift.ts.sql.db.infra;

public class ExternalPostgresql extends AbstractExternalSqlDatabaseAndConfigMapStrategy {
    public ExternalPostgresql() {
        super("postgresql96&&geo_RDU", "target/test-classes/project-defaults-external-postgresql.yml");
    }
}
