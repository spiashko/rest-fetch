package com.spiashko.jpafetch.demo.rest;

import com.spiashko.jpafetch.demo.BaseApplicationTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;


class GetCatEndpointSecurityTest extends BaseApplicationTest {

    @Test
    @DisplayName("when cat GET endpoint invoked with filter param which contains forbidden parts then error is received")
    void whenGetWithForbiddenFilterParam_thenErrorIsReceived() {
        // @formatter:off
        RestAssured
                .given()
                    .queryParam("filter", "owner.name==bob")
                .when()
                    .get("/cats")
                .then()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        // @formatter:on
    }

    @Test
    @DisplayName("when cat GET endpoint invoked with include param which contains forbidden parts then error is received")
    void whenGetWithForbiddenIncludeParam_thenErrorIsReceived() {
        // @formatter:off
        RestAssured
                .given()
                    .queryParam("include", "owner")
                .when()
                    .get("/cats")
                .then()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        // @formatter:on
    }

}
