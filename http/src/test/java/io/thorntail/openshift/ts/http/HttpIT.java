package io.thorntail.openshift.ts.http;

import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@RunWith(Arquillian.class)
public class HttpIT {
    @RouteURL(value = "${app.name}", path = "/api/greeting")
    @AwaitRoute
    private String url;

    @Test
    public void simpleInvocation() {
        given()
                .baseUri(url)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello, World!"));
    }

    @Test
    public void invocationWithParam() {
        given()
                .baseUri(url)
                .queryParam("name", "Peter")
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello, Peter!"));
    }
}
