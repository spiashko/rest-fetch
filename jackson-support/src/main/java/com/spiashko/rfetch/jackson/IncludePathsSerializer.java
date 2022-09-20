package com.spiashko.rfetch.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.HashMap;


@Slf4j
@RequiredArgsConstructor
public class IncludePathsSerializer extends JsonSerializer<Object>
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

        if (!beanDescription.getClassInfo().hasAnnotation(Entity.class) ||
                shouldSerAsUsual(value, gen)) {
            defaultSerializer.serialize(value, gen, serializers);
            return;
        }

        // ser only id
        Class<?> beanClass = beanDescription.getBeanClass();
        Field field = FieldUtils.getFieldsWithAnnotation(beanClass, Id.class)[0];
        ReflectionUtils.makeAccessible(field);
        Object id = ReflectionUtils.getField(field, value);
        HashMap<String, Object> map = new HashMap<>();
        map.put(field.getName(), id);
        mapSerializer.serialize(map, gen, serializers);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> del = prov.handleSecondaryContextualization(defaultSerializer, property);
        if (del == defaultSerializer) {
            return this;
        }
        return new IncludePathsSerializer((JsonSerializer<Object>) del, beanDescription);
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

        String pathToTest = IncludePathsUtils.getPathToTest(jgen);
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
