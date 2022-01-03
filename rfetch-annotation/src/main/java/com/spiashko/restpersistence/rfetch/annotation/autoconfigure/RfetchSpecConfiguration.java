package com.spiashko.restpersistence.rfetch.annotation.autoconfigure;


import com.spiashko.restpersistence.rfetch.annotation.RfetchSpecArgumentResolver;
import com.spiashko.restpersistence.rfetch.annotation.RfetchValueCustomizer;
import com.spiashko.restpersistence.rfetch.annotation.RfetchValueInterceptor;
import com.spiashko.restpersistence.rfetch.core.RfetchSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RfetchSpecConfiguration implements WebMvcConfigurer {

    private final List<RfetchValueCustomizer> valueCustomizers;
    private final List<RfetchValueInterceptor> valueValidators;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new RfetchSpecArgumentResolver(rfetchSupport(), valueCustomizers, valueValidators));
    }

    @Bean
    public RfetchSupport rfetchSupport() {
        return new RfetchSupport();
    }

}
