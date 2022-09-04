package com.spiashko.rfetch.demo.config;

import com.spiashko.rfetch.jpa.layered.LayeredFetchTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class GeneralConfig {

    @Bean
    public LayeredFetchTemplate fetchSmartTemplate(EntityManager entityManager) {
        return new LayeredFetchTemplate(entityManager);
    }

}
