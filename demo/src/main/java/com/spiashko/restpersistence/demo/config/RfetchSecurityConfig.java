package com.spiashko.restpersistence.demo.config;

import com.spiashko.restpersistence.rfetch.annotation.security.RfetchJsonViewSecurityInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RfetchSecurityConfig {


    @Bean
    public RfetchJsonViewSecurityInterceptor rfetchJsonViewSecurityValidator() {
        return new RfetchJsonViewSecurityInterceptor();
    }

}
