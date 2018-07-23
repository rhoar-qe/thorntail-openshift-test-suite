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

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;

import java.io.IOException;
import java.net.URL;

import org.arquillian.cube.kubernetes.api.Session;
import org.arquillian.cube.openshift.impl.client.OpenShiftAssistant;
import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.fabric8.openshift.client.OpenShiftClient;
import io.restassured.RestAssured;

public abstract class AbstractDBOpenshiftTest {

    protected static final String APP_NAME = System.getProperty("app.name");

    protected static final String DB_APP_NAME="testdb";

    @RouteURL(value = "${app.name}", path = "/rest/sqldb")
    protected URL url;

    @RouteURL(value = "${app.name}", path = "/health")
    @AwaitRoute
    protected URL healthUrl;

    @ArquillianResource
    protected OpenShiftClient oc;

    @ArquillianResource
    protected Session session;

    @ArquillianResource
    protected OpenShiftAssistant openShiftAssistant;

    protected void initDB() throws Exception {}

    protected void cleanUpDb() {}

    @Before
    public void setup() throws Exception {
        RestAssured.baseURI = url.toString();

        initDB();
    }

    @After
    public void cleanUp() {
        openShiftAssistant.cleanup();

        cleanUpDb();
    }

    @Test
    public void testDB() throws IOException, InterruptedException {

        when().
            get("/selectall").
        then().
            assertThat().
                statusCode(200).
            assertThat().
                body(containsString("OK"));

        when().
            get("/query").
        then().
            assertThat().
                statusCode(200).
            assertThat()
                .body(containsString("OK"));

        when().
            get("/update").
        then().
            assertThat().
                statusCode(200).
            assertThat()
                .body(containsString("OK"));

        when().
            get("/delete").
        then().
            assertThat().
                statusCode(200).
            assertThat().
                body(containsString("OK"));

    }
}
