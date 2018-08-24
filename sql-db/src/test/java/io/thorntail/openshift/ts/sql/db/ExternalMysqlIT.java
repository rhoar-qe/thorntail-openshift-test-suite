package io.thorntail.openshift.ts.sql.db;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ExternalMysqlIT extends AbstractExternalSqlDatabaseTest {
    public ExternalMysqlIT() {
        super("mysql57&&geo_BOS", "target/test-classes/project-defaults-external-mysql.yml");
    }
}
