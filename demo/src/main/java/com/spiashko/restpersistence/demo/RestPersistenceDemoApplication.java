package com.spiashko.restpersistence.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class RestPersistenceDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestPersistenceDemoApplication.class, args);
    }

}
