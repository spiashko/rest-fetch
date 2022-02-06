package com.spiashko.jpafetch.security.autoconfigure;

import com.spiashko.jpafetch.security.JsonViewSecurityInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaSecurityConfiguration {

    @Bean
    public JsonViewSecurityInterceptor jsonViewSecurityInterceptor() {
        return new JsonViewSecurityInterceptor();
    }

}
