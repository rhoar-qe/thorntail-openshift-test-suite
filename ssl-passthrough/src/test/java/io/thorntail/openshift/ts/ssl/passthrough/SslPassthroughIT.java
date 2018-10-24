package io.thorntail.openshift.ts.ssl.passthrough;

import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@RunWith(Arquillian.class)
public class SslPassthroughIT {
    @RouteURL(value = "${app.name}")
    @AwaitRoute(path = "/health")
    private String url;

    @RouteURL(value = "secured-${app.name}")
    private String securedUrl;

    @Test
    public void unsecuredRoute() {
        given()
                .baseUri(url)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello on port 8080, secure: false"));
    }

    @Test
    public void sslPassThrough() {
        given()
                .baseUri(securedUrl)
                .relaxedHTTPSValidation()
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello on port 8443, secure: true"));
    }
}
