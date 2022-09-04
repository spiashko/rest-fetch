package com.spiashko.rfetch.jpa.configs;

import com.spiashko.rfetch.jpa.layered.LayeredFetchTemplate;
import com.spiashko.rfetch.jpa.smart.SmartFetchTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;


@Configuration
public class GeneralConfig {

    @Bean
    public LayeredFetchTemplate layeredFetchTemplate(EntityManager entityManager) {
        return new LayeredFetchTemplate(entityManager);
    }

    @Bean
    public SmartFetchTemplate smartFetchTemplate(EntityManager entityManager) {
        return new SmartFetchTemplate(entityManager);
    }
}
