package com.spiashko.jpafetch.jacksonjpa.selfrefresolution;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Entity;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
public class SelfReferenceResolutionSerializer extends JsonSerializer<Object>
        implements ContextualSerializer, ResolvableSerializer {

    private final JsonSerializer<Object> defaultSerializer;
    private final BeanDescription beanDescription;

    private JsonSerializer<Object> mapSerializer;

    @SneakyThrows
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) {
        if (mapSerializer == null) {
            mapSerializer = PropertySerializerMap.emptyForProperties()
                    .findAndAddPrimarySerializer(HashMap.class, serializers, null)
                    .serializer;
        }

        if (!beanDescription.getClassInfo().hasAnnotation(Entity.class)) {
            defaultSerializer.serialize(value, gen, serializers);
            return;
        }

        if (shouldSerAsUsual(value, gen)) {
            defaultSerializer.serialize(value, gen, serializers);
        } else { // ser only id
            Class<?> beanClass = beanDescription.getBeanClass();
            //TODO: remove hardcoded method
            Method getId = ReflectionUtils.findMethod(beanClass, "getId");
            Object id = Objects.requireNonNull(getId).invoke(value);

            HashMap<String, Object> map = new HashMap<>();
            //TODO: remove hardcoded id property name
            map.put("id", id);
            mapSerializer.serialize(map, gen, serializers);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> del = prov.handleSecondaryContextualization(defaultSerializer, property);
        if (del == defaultSerializer) {
            return this;
        }
        return new SelfReferenceResolutionSerializer((JsonSerializer<Object>) del, beanDescription);
    }

    @Override
    public void resolve(SerializerProvider provider) throws JsonMappingException {
        if (defaultSerializer instanceof ResolvableSerializer) {
            ((ResolvableSerializer) defaultSerializer).resolve(provider);
        }
    }


    private boolean shouldSerAsUsual(final Object pojo, final JsonGenerator jgen) {
        // pass to default as it should be handled by other serializers
        if (pojo == null || pojo instanceof HibernateProxy) {
            return true;
        }

        String pathToTest = SelfReferenceResolutionUtils.getPathToTest(jgen);
        //it is root therefore we pass to let serialization of entity be started
        if ("".equals(pathToTest)) {
            return true;
        }
        //as it is not root then some relation and as include is empty then we serialise only id
        if (CollectionUtils.isEmpty(IncludePathsHolder.getIncludedPaths())) {
            return false;
        }
        return IncludePathsHolder.getIncludedWithSubPaths().stream()
                .anyMatch(pathToTest::equals);
    }

}
