package io.thorntail.openshift.ts.sql.db.infra;

import java.io.File;

public class Postgresql extends AbstractInternalSqlDatabaseAndConfigMapStrategy {
    public Postgresql() {
        super(
                "registry.access.redhat.com/rhscl/postgresql-10-rhel7",
                "postgresql.image",
                new File("target/test-classes/project-defaults-postgresql.yml"),
                mapOf(
                        "POSTGRESQL_DATABASE", "testdb",
                        "POSTGRESQL_USER", "testuser",
                        "POSTGRESQL_PASSWORD", "password"
                )
        );
    }
}
