package com.spiashko.restpersistence.rfetch.autoconfigure;


import com.spiashko.restpersistence.rfetch.RfetchPathsHolder;
import com.spiashko.restpersistence.rfetch.RfetchSpecArgumentResolver;
import com.spiashko.restpersistence.rfetch.RfetchValueCustomizer;
import com.spiashko.restpersistence.rfetch.RfetchValueValidator;
import com.spiashko.restpersistence.rfetch.security.RfetchJsonViewSecurityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RfetchSpecConfiguration implements WebMvcConfigurer {

    private final List<RfetchValueCustomizer> valueCustomizers;
    private final List<RfetchValueValidator> valueValidators;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new RfetchSpecArgumentResolver(rfetchPathsHolder(), valueCustomizers, valueValidators));
    }

    @Bean
    @RequestScope
    public RfetchPathsHolder rfetchPathsHolder() {
        return new RfetchPathsHolder();
    }

}
