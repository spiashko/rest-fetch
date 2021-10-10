package com.spiashko.restpersistence.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaAuditing
@EnableTransactionManagement
@SpringBootApplication
public class RestPersistenceDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestPersistenceDemoApplication.class, args);
    }

}
