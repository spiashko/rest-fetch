package com.spiashko.jpafetch.demo.config;

import com.spiashko.jpafetch.fetch.FetchSmartTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class GeneralConfig {

    @Bean
    public FetchSmartTemplate fetchSmartTemplate(EntityManager entityManager) {
        return new FetchSmartTemplate(entityManager);
    }

}
