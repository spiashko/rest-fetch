package com.spiashko.restpersistence.rsqlspec.annotation.autoconfigure;


import com.spiashko.restpersistence.rsqlspec.annotation.RsqlSpecArgumentResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
public class RsqlSpecConfiguration implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new RsqlSpecArgumentResolver());
    }

}
