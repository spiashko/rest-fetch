package com.spiashko.restpersistence.demo.rfetchmodule;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.spiashko.restpersistence.rfetch.RfetchPathsHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.Entity;
import java.io.IOException;
import java.util.HashMap;


@Slf4j
@RequiredArgsConstructor
public class SelfReferenceResolutionSerializer extends JsonSerializer<Object>
        implements ContextualSerializer, ResolvableSerializer {

    private final RfetchPathsHolder rfetchPathsHolder;
    private final JsonSerializer<Object> defaultSerializer;
    private final BeanDescription beanDescription;
    private PropertySerializerMap _dynamicSerializers = PropertySerializerMap.emptyForProperties();

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (!beanDescription.getClassInfo().hasAnnotation(Entity.class)) {
            defaultSerializer.serialize(value, gen, serializers);
            return;
        }

        if (shouldSerAsUsual(value, gen)) {
            defaultSerializer.serialize(value, gen, serializers);
        } else { // ser only id

            HashMap<String, Object> map = new HashMap<>();
            map.put("kek", "lol");

            JsonSerializer<Object> serializer = findSerializer(serializers, map);
            serializer.serialize(map, gen, serializers);
        }
    }

    protected boolean shouldSerAsUsual(final Object pojo, final JsonGenerator jgen) {
        String pathToTest = getPathToTest(jgen);

        if (pojo == null) {
            return true;
        }

        if ("null".equals(pathToTest)) {
            return true;
        }

        if (pojo instanceof HibernateProxy) {
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
        if (sc != null) {
            nestedPath.insert(0, sc.getCurrentName());
            sc = sc.getParent();
        }
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


    protected JsonSerializer<Object> findSerializer(SerializerProvider provider, Object value)
            throws IOException {
        /* TODO: if Hibernate did use generics, or we wanted to allow use of Jackson
         *  annotations to indicate type, should take that into account.
         */
        Class<?> type = value.getClass();
        /* we will use a map to contain serializers found so far, keyed by type:
         * this avoids potentially costly lookup from global caches and/or construction
         * of new serializers
         */
        /* 18-Oct-2013, tatu: Whether this is for the primary property or secondary is
         *   really anyone's guess at this point; proxies can exist at any level?
         */
        PropertySerializerMap.SerializerAndMapResult result =
                _dynamicSerializers.findAndAddPrimarySerializer(type, provider, null);
        if (_dynamicSerializers != result.map) {
            _dynamicSerializers = result.map;
        }

        return result.serializer;
    }
}
