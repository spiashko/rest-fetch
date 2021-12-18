package com.spiashko.restpersistence.jacksonjpa.selfrefresolution;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.spiashko.restpersistence.rfetch.RfetchPathsHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.lang.annotation.Annotation;


@Slf4j
@RequiredArgsConstructor
public class SelfReferenceResolutionFilter extends SimpleBeanPropertyFilter {

    @SuppressWarnings("unchecked")
    private static Class<? extends Annotation>[] TO_MANY_ANNOTATIONS = (Class<? extends Annotation>[])
            new Class<?>[]{
                    ManyToMany.class,
                    OneToMany.class
            };

    private final RfetchPathsHolder rfetchPathsHolder;


    /**
     * Gets the path to test.
     *
     * @param writer the writer
     * @param jgen   the jgen
     * @return the path to test
     */
    private String getPathToTest(final PropertyWriter writer, final JsonGenerator jgen) {
        StringBuilder nestedPath = new StringBuilder();
        nestedPath.append(writer.getName());
        JsonStreamContext sc = jgen.getOutputContext();
        if (sc != null) {
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

    /*
     * (non-Javadoc)
     *
     * @see
     * com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter#include(
     * com.fasterxml.jackson.databind.ser. BeanPropertyWriter)
     */
    @Override
    protected boolean include(final BeanPropertyWriter writer) {
        throw new UnsupportedOperationException("Cannot call include without JsonGenerator");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter#include(
     * com.fasterxml.jackson.databind.ser. PropertyWriter)
     */
    @Override
    protected boolean include(final PropertyWriter writer) {
        throw new UnsupportedOperationException("Cannot call include without JsonGenerator");
    }

    /**
     * Include.
     *
     * @param writer the writer
     * @param jgen   the jgen
     * @return true, if successful
     */
    protected boolean shouldSer(final PropertyWriter writer, final JsonGenerator jgen) {
        String pathToTest = getPathToTest(writer, jgen);

        AnnotatedMember member = writer.getMember();

        if (member == null) {
            return true;
        }

        if (!member.hasOneOf(TO_MANY_ANNOTATIONS)) {
            return true;
        }


        if (rfetchPathsHolder.getIncludedPaths() == null) {
            return false;
        }

        return rfetchPathsHolder.getIncludedPaths().stream().anyMatch(pathToTest::equals);
    }

    @Override
    public void serializeAsField(final Object pojo, final JsonGenerator jgen, final SerializerProvider provider,
                                 final PropertyWriter writer) throws Exception {

        if (shouldSer(writer, jgen)) {
            writer.serializeAsField(pojo, jgen, provider);
        } else {
            writer.serializeAsOmittedField(pojo, jgen, provider);
        }
    }
}
