package com.spiashko.restpersistence.demo.config;

import com.spiashko.restpersistence.rfetch.security.RfetchJsonViewSecurityValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RfetchSecurityConfig {


    @Bean
    public RfetchJsonViewSecurityValidator rfetchJsonViewSecurityValidator() {
        return new RfetchJsonViewSecurityValidator();
    }

}
