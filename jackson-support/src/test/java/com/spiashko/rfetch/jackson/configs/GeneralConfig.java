package com.spiashko.rfetch.jackson.configs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spiashko.rfetch.jackson.IncludePathsFilter;
import com.spiashko.rfetch.jackson.IncludePathsFilterMixin;
import com.spiashko.rfetch.jackson.autoconfigure.IncludePathsJacksonConfiguration;
import com.spiashko.rfetch.jpa.smart.SmartFetchTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;


@Import(IncludePathsJacksonConfiguration.class)
@Configuration
public class GeneralConfig {

    @Bean
    public ObjectMapper objectMapper(Hibernate5Module hibernate5Module,
                                     SimpleModule includePathsSerializerModule) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
        objectMapper.registerModule(hibernate5Module);
        objectMapper.registerModule(includePathsSerializerModule);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.addMixIn(Object.class, IncludePathsFilterMixin.class);
        objectMapper.setFilterProvider(new SimpleFilterProvider()
                .addFilter(IncludePathsFilter.NAME, new IncludePathsFilter()));
        return objectMapper;
    }

    @Bean
    public SmartFetchTemplate fetchSmartTemplate(EntityManager entityManager) {
        return new SmartFetchTemplate(entityManager);
    }

}
