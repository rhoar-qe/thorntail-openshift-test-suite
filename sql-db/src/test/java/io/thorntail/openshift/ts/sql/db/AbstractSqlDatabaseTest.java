package io.thorntail.openshift.ts.sql.db;

import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public abstract class AbstractSqlDatabaseTest {
    @RouteURL(value = "${app.name}", path = "/api/sqldb")
    @AwaitRoute
    private String url;

    @Test
    public void selectOne() {
        given()
                .baseUri(url)
        .when()
                .get("/select-one")
        .then()
                .statusCode(200)
                .body(containsString("OK"));

    }

    @Test
    public void selectMany() {
        given()
                .baseUri(url)
        .when()
                .get("/select-many")
        .then()
                .statusCode(200)
                .body(containsString("OK"));
    }

    @Test
    public void update() {
        given()
                .baseUri(url)
        .when()
                .get("/update")
        .then()
                .statusCode(200)
                .body(containsString("OK"));
    }

    @Test
    public void delete() {
        given()
                .baseUri(url)
        .when()
                .get("/delete")
        .then()
                .statusCode(200)
                .body(containsString("OK"));
    }
}
