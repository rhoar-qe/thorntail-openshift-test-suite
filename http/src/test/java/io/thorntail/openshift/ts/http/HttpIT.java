package io.thorntail.openshift.ts.http;

import io.restassured.RestAssured;
import io.thorntail.openshift.test.OpenShiftTest;
import io.thorntail.openshift.test.injection.TestResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;

@OpenShiftTest
public class HttpIT {
    @TestResource
    private URL url;

    @BeforeEach
    public void setUp() {
        RestAssured.basePath = "/api/greeting";
    }

    @Test
    public void simpleInvocation() {
        when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello, World!"));
    }

    @Test
    public void invocationWithParam() {
        given()
                .queryParam("name", "Peter")
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello, Peter!"));
    }
}
