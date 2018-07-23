package io.thorntail.openshift.ts.sql.db;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(Arquillian.class)
public class MysqlIT extends AbstractInternalSqlDatabaseTest {
    public MysqlIT() {
        super(
                "registry.access.redhat.com/rhscl/mysql-57-rhel7",
                new File("target/test-classes/project-defaults-mysql.yml"),
                mapOf(
                        "MYSQL_DATABASE", "testdb",
                        "MYSQL_USER", "testuser",
                        "MYSQL_PASSWORD", "password"
                )
        );
    }
}
