package com.spiashko.restpersistence.demo.rfetchmodule;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.spiashko.restpersistence.rfetch.RfetchPathsHolder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Entity;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
public class SelfReferenceResolutionSerializer extends JsonSerializer<Object>
        implements ContextualSerializer, ResolvableSerializer {

    private final RfetchPathsHolder rfetchPathsHolder;
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
            Method getId = ReflectionUtils.findMethod(beanClass, "getId");
            Object id = Objects.requireNonNull(getId).invoke(value);

            HashMap<String, Object> map = new HashMap<>();
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
        return new SelfReferenceResolutionSerializer(rfetchPathsHolder, (JsonSerializer<Object>) del, beanDescription);
    }

    @Override
    public void resolve(SerializerProvider provider) throws JsonMappingException {
        if (defaultSerializer instanceof ResolvableSerializer) {
            ((ResolvableSerializer) defaultSerializer).resolve(provider);
        }
    }


    private boolean shouldSerAsUsual(final Object pojo, final JsonGenerator jgen) {
        String pathToTest = getPathToTest(jgen);

        if (pojo == null) {
            return true;
        }

        if (pojo instanceof HibernateProxy) {
            return true;
        }

        if ("".equals(pathToTest)) {
            return true;
        }

        if (rfetchPathsHolder.getIncludedPaths() == null) {
            return false;
        }

        return rfetchPathsHolder.getIncludedPaths().stream().anyMatch(pathToTest::equals);
    }


    private String getPathToTest(final JsonGenerator jgen) {
        StringBuilder nestedPath = new StringBuilder();
        JsonStreamContext sc = jgen.getOutputContext();
        while (sc != null) {
            if (sc.getCurrentName() != null) {
                if (nestedPath.length() > 0) {
                    nestedPath.insert(0, ".");
                }
                nestedPath.insert(0, sc.getCurrentName());
            }
            sc = sc.getParent();
        }
        return nestedPath.toString();
    }
}
