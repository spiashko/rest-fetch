package com.spiashko.restpersistence.rsqlspec.annotation.autoconfigure;


import com.spiashko.restpersistence.rsqlspec.annotation.RsqlSpecArgumentResolver;
import com.spiashko.restpersistence.rsqlspec.annotation.RsqlValueCustomizer;
import com.spiashko.restpersistence.rsqlspec.annotation.RsqlValueInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RsqlSpecConfiguration implements WebMvcConfigurer {

    private final List<RsqlValueCustomizer> valueCustomizers;
    private final List<RsqlValueInterceptor> valueInterceptors;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new RsqlSpecArgumentResolver(valueCustomizers, valueInterceptors));
    }

}
