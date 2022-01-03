package com.spiashko.restpersistence.jacksonjpa.autoconfigure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.spiashko.restpersistence.jacksonjpa.entitybyid.EntityByIdDeserializer;
import com.spiashko.restpersistence.jacksonjpa.selfrefresolution.SelfReferenceResolutionConstants;
import com.spiashko.restpersistence.jacksonjpa.selfrefresolution.SelfReferenceResolutionFilter;
import com.spiashko.restpersistence.jacksonjpa.selfrefresolution.SelfReferenceResolutionFilterMixin;
import com.spiashko.restpersistence.jacksonjpa.selfrefresolution.SelfReferenceResolutionSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

import javax.persistence.EntityManager;

@Configuration
public class JacksonJpaConfiguration {

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
    public SimpleModule entityByIdDeserializerModule(EntityManager entityManager,
                                                     ConversionService conversionService) {
        SimpleModule module = new SimpleModule("entityByIdDeserializerModule");
        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
                                                          BeanDescription beanDescription,
                                                          JsonDeserializer<?> originalDeserializer) {
                return new EntityByIdDeserializer(originalDeserializer, entityManager, beanDescription,
                        conversionService);
            }
        });
        return module;
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
    public Jackson2ObjectMapperBuilderCustomizer addRfetchPropertyFilterCustomizer() {
        return builder -> {
            builder.filters(new SimpleFilterProvider()
                    .addFilter(SelfReferenceResolutionConstants.SELF_REFERENCE_RESOLUTION_FILTER,
                            new SelfReferenceResolutionFilter()));
            builder.mixIn(Object.class, SelfReferenceResolutionFilterMixin.class);
        };
    }
}
