package com.spiashko.rfetch.jpa.configs;

import com.spiashko.rfetch.jpa.smart.FetchSmartTemplate;
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
