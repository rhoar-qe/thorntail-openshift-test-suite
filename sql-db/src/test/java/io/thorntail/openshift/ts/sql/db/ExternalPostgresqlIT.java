package io.thorntail.openshift.ts.sql.db;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ExternalPostgresqlIT extends AbstractExternalSqlDatabaseTest {
    public ExternalPostgresqlIT() {
        super("postgresql96&&geo_RDU", "target/test-classes/project-defaults-external-postgresql.yml");
    }
}
