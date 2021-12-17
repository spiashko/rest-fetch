package com.spiashko.restpersistence.demo.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.spiashko.restpersistence.demo.rfetchmodule.SelfReferenceResolutionSerializer;
import com.spiashko.restpersistence.rfetch.RfetchPathsHolder;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }

    @Bean
    public SimpleModule selfReferenceResolutionSerializerModule(RfetchPathsHolder rfetchPathsHolder) {
        SimpleModule module = new SimpleModule("selfReferenceResolverSerializerModule");
        module.setSerializerModifier(new BeanSerializerModifier() {
            @SuppressWarnings("unchecked")
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> originalSerializer) {
                return new SelfReferenceResolutionSerializer(rfetchPathsHolder, (JsonSerializer<Object>) originalSerializer, beanDesc);
            }
        });
        return module;
    }

}
