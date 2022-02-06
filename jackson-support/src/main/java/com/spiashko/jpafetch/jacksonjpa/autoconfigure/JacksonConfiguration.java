package com.spiashko.jpafetch.jacksonjpa.autoconfigure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.spiashko.jpafetch.jacksonjpa.selfrefresolution.core.SelfReferenceResolutionConstants;
import com.spiashko.jpafetch.jacksonjpa.selfrefresolution.core.SelfReferenceResolutionFilter;
import com.spiashko.jpafetch.jacksonjpa.selfrefresolution.core.SelfReferenceResolutionFilterMixin;
import com.spiashko.jpafetch.jacksonjpa.selfrefresolution.core.SelfReferenceResolutionSerializer;
import com.spiashko.jpafetch.jacksonjpa.selfrefresolution.servlet.IncludePathsFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

@Configuration
public class JacksonConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public Hibernate5Module hibernate5Module() {
        Hibernate5Module module = new Hibernate5Module();
        module.enable(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
        module.disable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
        return module;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }

    @Bean
    public SimpleModule selfReferenceResolutionSerializerModule() {
        SimpleModule module = new SimpleModule("selfReferenceResolverSerializerModule");
        module.setSerializerModifier(new BeanSerializerModifier() {
            @SuppressWarnings("unchecked")
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> originalSerializer) {
                return new SelfReferenceResolutionSerializer((JsonSerializer<Object>) originalSerializer, beanDesc);
            }
        });
        return module;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addSelfReferenceResolutionFilterCustomizer() {
        return builder -> {
            builder.filters(new SimpleFilterProvider()
                    .addFilter(SelfReferenceResolutionConstants.SELF_REFERENCE_RESOLUTION_FILTER,
                            new SelfReferenceResolutionFilter()));
            builder.mixIn(Object.class, SelfReferenceResolutionFilterMixin.class);
        };
    }

    @Bean
    public IncludePathsFilter includePathsFilter() {
        return new IncludePathsFilter();
    }

    @Bean
    public FilterRegistrationBean<IncludePathsFilter> includePathsFilterRegistrationBean(IncludePathsFilter includePathsFilter) {
        FilterRegistrationBean<IncludePathsFilter> registration = new FilterRegistrationBean<>(includePathsFilter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration
                .setName("includePathsFilter");
        registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
        return registration;
    }
}
