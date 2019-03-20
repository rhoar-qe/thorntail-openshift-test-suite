package io.thorntail.openshift.ts.sql.db.infra;

public class ExternalMysql extends AbstractExternalSqlDatabaseAndConfigMapStrategy {
    public ExternalMysql() {
        super("mysql57&&geo_BOS", "target/test-classes/project-defaults-external-mysql.yml");
    }
}
