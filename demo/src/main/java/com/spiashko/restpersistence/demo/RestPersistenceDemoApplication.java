package com.spiashko.restpersistence.demo;

import com.spiashko.restpersistence.repo.FetchSmartRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaRepositories(
        repositoryBaseClass = FetchSmartRepositoryImpl.class)
@EnableTransactionManagement
@SpringBootApplication
public class RestPersistenceDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestPersistenceDemoApplication.class, args);
    }

}
