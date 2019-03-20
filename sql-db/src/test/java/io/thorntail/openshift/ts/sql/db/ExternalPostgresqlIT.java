package io.thorntail.openshift.ts.sql.db;

import io.thorntail.openshift.ts.sql.db.arquillian.SqlDatabaseAndConfigMap;
import io.thorntail.openshift.ts.sql.db.infra.ExternalPostgresql;
import org.arquillian.cube.openshift.api.OpenShiftResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@SqlDatabaseAndConfigMap(ExternalPostgresql.class)
@OpenShiftResource("file:target/classes/META-INF/fabric8/openshift.yml")
public class ExternalPostgresqlIT extends AbstractSqlDatabaseTest {
}
