package com.spiashko.rfetch.demo.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.spiashko.rfetch.demo.selfrefresolution.servlet.CleanIncludePathsFilter;
import com.spiashko.rfetch.jackson.IncludePathsFilter;
import com.spiashko.rfetch.jackson.IncludePathsFilterMixin;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

@Configuration
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addIncludePathsFilterCustomizer() {
        return builder -> {
            builder.filters(new SimpleFilterProvider()
                    .addFilter(IncludePathsFilter.NAME, new IncludePathsFilter()));
            builder.mixIn(Object.class, IncludePathsFilterMixin.class);
        };
    }

    @Bean
    public CleanIncludePathsFilter cleanIncludePathsFilter() {
        return new CleanIncludePathsFilter();
    }

    @Bean
    public FilterRegistrationBean<CleanIncludePathsFilter> includePathsFilterRegistrationBean(CleanIncludePathsFilter cleanIncludePathsFilter) {
        FilterRegistrationBean<CleanIncludePathsFilter> registration = new FilterRegistrationBean<>(cleanIncludePathsFilter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration
                .setName("includePathsFilter");
        registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
        return registration;
    }
}
