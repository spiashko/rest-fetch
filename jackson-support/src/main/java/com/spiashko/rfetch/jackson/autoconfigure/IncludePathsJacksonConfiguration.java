package com.spiashko.rfetch.jackson.autoconfigure;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.spiashko.rfetch.jackson.IncludePathsSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IncludePathsJacksonConfiguration {

    public static final String INCLUDE_PATHS_SERIALIZER_MODULE_NAME = "includePathsSerializerModule";

    @ConditionalOnMissingBean
    @Bean
    public Hibernate5Module hibernate5Module() {
        Hibernate5Module module = new Hibernate5Module();
        module.enable(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
        module.disable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
        return module;
    }

    @Bean
    public SimpleModule includePathsSerializerModule() {
        SimpleModule module = new SimpleModule(INCLUDE_PATHS_SERIALIZER_MODULE_NAME);
        module.setSerializerModifier(new BeanSerializerModifier() {
            @SuppressWarnings("unchecked")
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> originalSerializer) {
                return new IncludePathsSerializer((JsonSerializer<Object>) originalSerializer, beanDesc);
            }
        });
        return module;
    }

}
