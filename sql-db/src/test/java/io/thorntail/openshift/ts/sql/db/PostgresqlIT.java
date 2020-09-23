package io.thorntail.openshift.ts.sql.db;

import io.thorntail.openshift.ts.common.arquillian.ProjectCleanup;
import io.thorntail.openshift.ts.sql.db.arquillian.SqlDatabaseAndConfigMap;
import io.thorntail.openshift.ts.sql.db.infra.Postgresql;
import org.arquillian.cube.openshift.api.OpenShiftResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@ProjectCleanup
@SqlDatabaseAndConfigMap(Postgresql.class)
@OpenShiftResource("file:target/classes/META-INF/jkube/openshift.yml")
public class PostgresqlIT extends AbstractSqlDatabaseTest {
}
