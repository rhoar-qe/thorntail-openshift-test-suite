package io.thorntail.openshift.ts.sql.db;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ExternalOracleIT extends AbstractExternalSqlDatabaseTest {
    public ExternalOracleIT() {
        super("oracle12cR2&&geo_BOS", "target/test-classes/project-defaults-external-oracle.yml");
    }
}
