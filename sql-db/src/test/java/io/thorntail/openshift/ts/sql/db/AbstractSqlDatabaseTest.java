package io.thorntail.openshift.ts.sql.db;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.when;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;

public abstract class AbstractSqlDatabaseTest {
    @BeforeEach
    public void setUp() {
        RestAssured.basePath = "/api/sqldb";

        await().atMost(5, TimeUnit.MINUTES).untilAsserted(() -> {
            when()
                    .get()
            .then()
                    .statusCode(200);
        });
    }

    @Test
    public void selectOne() {
        when()
                .get("/select-one")
        .then()
                .statusCode(200)
                .body(containsString("OK"));

    }

    @Test
    public void selectMany() {
        when()
                .get("/select-many")
        .then()
                .statusCode(200)
                .body(containsString("OK"));
    }

    @Test
    public void update() {
        when()
                .get("/update")
        .then()
                .statusCode(200)
                .body(containsString("OK"));
    }

    @Test
    public void delete() {
        when()
                .get("/delete")
        .then()
                .statusCode(200)
                .body(containsString("OK"));
    }
}
