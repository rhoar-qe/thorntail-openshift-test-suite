package io.thorntail.openshift.ts.sql.db;

import io.thorntail.openshift.test.CustomizeApplicationDeployment;
import io.thorntail.openshift.test.CustomizeApplicationUndeployment;
import io.thorntail.openshift.test.OpenShiftTest;
import io.thorntail.openshift.test.util.OpenShiftUtil;
import io.thorntail.openshift.ts.sql.db.infra.ExternalOracle;
import io.thorntail.openshift.ts.sql.db.infra.ProjectCleanup;

@OpenShiftTest
public class ExternalOracleIT extends AbstractSqlDatabaseTest {
    @CustomizeApplicationDeployment
    public static void deploy(OpenShiftUtil openshift) throws Exception {
        new ProjectCleanup().run();
        new ExternalOracle(openshift).deploy();
    }

    @CustomizeApplicationUndeployment
    public static void undeploy(OpenShiftUtil openshift) throws Exception {
        new ExternalOracle(openshift).undeploy();
    }
}
