package com.spiashko.restpersistence.demo.config;

import com.spiashko.restpersistence.rfetch.annotation.security.RfetchJsonViewSecurityInterceptor;
import com.spiashko.restpersistence.rsqlspec.annotation.security.RsqlJsonViewSecurityInterceptor;
import com.spiashko.restpersistence.security.JsonViewSecurityInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RAnnotationsSecurityConfig {


    @Bean
    public JsonViewSecurityInterceptor jsonViewSecurityInterceptor() {
        return new JsonViewSecurityInterceptor();
    }

    @Bean
    public RfetchJsonViewSecurityInterceptor rfetchJsonViewSecurityValidator(JsonViewSecurityInterceptor interceptor) {
        return new RfetchJsonViewSecurityInterceptor(interceptor);
    }

    @Bean
    public RsqlJsonViewSecurityInterceptor rsqlJsonViewSecurityInterceptor(JsonViewSecurityInterceptor interceptor) {
        return new RsqlJsonViewSecurityInterceptor(interceptor);
    }

}
