package io.thorntail.openshift.ts.sql.db.infra;

import java.io.File;

public class Mysql extends AbstractInternalSqlDatabaseAndConfigMapStrategy {
    public Mysql() {
        super(
                "registry.access.redhat.com/rhscl/mysql-80-rhel7",
                "mysql.image",
                new File("target/test-classes/project-defaults-mysql.yml"),
                mapOf(
                        "MYSQL_DATABASE", "testdb",
                        "MYSQL_USER", "testuser",
                        "MYSQL_PASSWORD", "password"
                )
        );
    }
}
