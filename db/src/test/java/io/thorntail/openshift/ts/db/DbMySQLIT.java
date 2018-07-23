/*
 *
 *  Copyright 2018 Red Hat, Inc, and individual contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.thorntail.openshift.ts.db;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

import io.thorntail.openshift.ts.utils.OC;
import io.thorntail.openshift.ts.utils.OpenShiftUtils;

@RunWith(Arquillian.class)
public class DbMySQLIT extends AbstractDBOpenshiftTest {

    @Override
    public void initDB() throws Exception {
        OC.execute("project", oc.getNamespace());
        OC.execute("new-app", "registry.access.redhat.com/rhscl/mysql-57-rhel7",
                "-e", "MYSQL_USER=testuser",
                "-e", "MYSQL_DATABASE=testdb",
                "-e", "MYSQL_PASSWORD=password",
          "--name=" + DB_APP_NAME);

        OpenShiftUtils.awaitAppDeployed(openShiftAssistant, DB_APP_NAME);

        OpenShiftUtils.deployConfigMapAndRollout(openShiftAssistant, "target/test-classes/project-defaults-mysql.yml", APP_NAME, url);
    }

    @Override
    public void cleanUpDb() {
        OC.execute("delete", "all","-l","app=" + DB_APP_NAME);
    }

}
