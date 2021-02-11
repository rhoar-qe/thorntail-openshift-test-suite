package io.thorntail.openshift.ts.ssl.passthrough;

import io.thorntail.openshift.test.AdditionalResources;
import io.thorntail.openshift.test.OpenShiftTest;
import io.thorntail.openshift.test.injection.TestResource;
import io.thorntail.openshift.test.injection.WithName;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@OpenShiftTest
@AdditionalResources("classpath:secret.yml")
public class SslPassthroughIT {
    @TestResource
    @WithName("ssl-passthrough")
    private URL url;

    @TestResource
    @WithName("secured-ssl-passthrough")
    private URL securedUrl;

    @Test
    public void unsecuredRoute() {
        given()
                .baseUri(url.toString())
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello on port 8080, secure: false"));
    }

    @Test
    public void sslPassThrough() {
        given()
                .baseUri(securedUrl.toString())
                .relaxedHTTPSValidation()
        .when()
                .get()
        .then()
                .statusCode(200)
                .body(containsString("Hello on port 8443, secure: true"));
    }
}
