package io.thorntail.openshift.ts.sql.db;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(Arquillian.class)
public class PostgresqlIT extends AbstractInternalSqlDatabaseTest {
    public PostgresqlIT() {
        super(
                "registry.access.redhat.com/rhscl/postgresql-95-rhel7",
                new File("target/test-classes/project-defaults-postgresql.yml"),
                mapOf(
                        "POSTGRESQL_DATABASE", "testdb",
                        "POSTGRESQL_USER", "testuser",
                        "POSTGRESQL_PASSWORD", "password"
                )
        );
    }
}
