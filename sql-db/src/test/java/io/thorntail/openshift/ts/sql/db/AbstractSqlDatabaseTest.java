package io.thorntail.openshift.ts.sql.db;

import io.fabric8.openshift.client.OpenShiftClient;
import io.thorntail.openshift.ts.common.arquillian.OpenShiftUtil;
import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public abstract class AbstractSqlDatabaseTest {
    protected static final String APP_NAME = System.getProperty("app.name");
    protected static final String DB_APP_NAME = "test-db";

    @RouteURL(value = "${app.name}", path = "/api/sqldb")
    @AwaitRoute(path = "/health")
    protected URL url;

    @ArquillianResource
    protected OpenShiftClient oc;

    @ArquillianResource
    protected OpenShiftUtil openshift;

    protected abstract void createDb() throws Exception;

    protected abstract void dropDb() throws Exception;

    @Test
    @InSequence(1)
    public void setUp() throws Exception {
        createDb();
    }

    @Test
    @InSequence(10)
    public void selectOne() {
        given()
                .baseUri(url.toString())
        .when()
                .get("/select-one")
        .then()
                .statusCode(200)
                .body(containsString("OK"));

    }

    @Test
    @InSequence(11)
    public void selectMany() {
        given()
                .baseUri(url.toString())
        .when()
                .get("/select-many")
        .then()
                .statusCode(200)
                .body(containsString("OK"));
    }

    @Test
    @InSequence(12)
    public void update() {
        given()
                .baseUri(url.toString())
        .when()
                .get("/update")
        .then()
                .statusCode(200)
                .body(containsString("OK"));
    }

    @Test
    @InSequence(13)
    public void delete() {
        given()
                .baseUri(url.toString())
        .when()
                .get("/delete")
        .then()
                .statusCode(200)
                .body(containsString("OK"));
    }

    @Test
    @InSequence(20)
    public void tearDown() throws Exception {
        dropDb();
    }
}
