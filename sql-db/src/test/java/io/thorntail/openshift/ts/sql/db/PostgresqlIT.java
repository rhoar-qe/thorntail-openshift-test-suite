package io.thorntail.openshift.ts.sql.db;

import io.thorntail.openshift.test.CustomizeApplicationDeployment;
import io.thorntail.openshift.test.CustomizeApplicationUndeployment;
import io.thorntail.openshift.test.OpenShiftTest;
import io.thorntail.openshift.test.util.OpenShiftUtil;
import io.thorntail.openshift.ts.sql.db.infra.Postgresql;
import io.thorntail.openshift.ts.sql.db.infra.ProjectCleanup;

import java.io.IOException;

@OpenShiftTest
public class PostgresqlIT extends AbstractSqlDatabaseTest {
    @CustomizeApplicationDeployment
    public static void deploy(OpenShiftUtil openshift) throws IOException, InterruptedException {
        new ProjectCleanup().run();
        new Postgresql(openshift).deploy();
    }

    @CustomizeApplicationUndeployment
    public static void undeploy(OpenShiftUtil openshift) throws IOException, InterruptedException {
        new Postgresql(openshift).undeploy();
    }
}
