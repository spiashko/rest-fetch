package com.spiashko.restpersistence.jacksonjpa.autoconfigure;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.spiashko.restpersistence.jacksonjpa.entitybyid.EntityByIdDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    public SimpleModule entityByIdDeserializerModule(EntityManager entityManager,
                                                     ConversionService conversionService) {
        SimpleModule module = new SimpleModule("entityByIdDeserializerModule");
        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
                                                          BeanDescription beanDescription,
                                                          JsonDeserializer<?> originalDeserializer) {
                return new EntityByIdDeserializer(originalDeserializer, entityManager, beanDescription, conversionService);
            }
        });
        return module;
    }

}
