package com.spiashko.restpersistence.demo;

import io.restassured.RestAssured;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.annotation.PostConstruct;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(scripts = {"classpath:sql-test-data/base-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BaseApplicationTest {

    private final static String BASE_URI = "http://localhost";

    public static JdbcDatabaseContainer<?> dbContainer = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("tests-db")
            .withUsername("sa")
            .withPassword("sa")
            .withInitScript("postgre-init.sql")
            .withEnv("TZ", "UTC")
            .withEnv("PGTZ", "UTC")
            .withReuse(true);

    static {
        dbContainer.start();
    }

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", dbContainer::getJdbcUrl);
        registry.add("spring.datasource.username", dbContainer::getUsername);
        registry.add("spring.datasource.password", dbContainer::getPassword);
    }

    @PostConstruct
    public void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

}
