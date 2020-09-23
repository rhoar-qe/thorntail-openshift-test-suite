package io.thorntail.openshift.ts.sql.db;

import io.thorntail.openshift.ts.common.arquillian.ProjectCleanup;
import io.thorntail.openshift.ts.sql.db.arquillian.SqlDatabaseAndConfigMap;
import io.thorntail.openshift.ts.sql.db.infra.ExternalOracle;
import org.arquillian.cube.openshift.api.OpenShiftResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@ProjectCleanup
@SqlDatabaseAndConfigMap(ExternalOracle.class)
@OpenShiftResource("file:target/classes/META-INF/jkube/openshift.yml")
public class ExternalOracleIT extends AbstractSqlDatabaseTest {
}
