package com.spiashko.rfetch.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.lang.annotation.Annotation;


@Slf4j
@RequiredArgsConstructor
public class IncludePathsFilter extends SimpleBeanPropertyFilter {

    public static final String NAME = "includePathsFilter";

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] TO_MANY_ANNOTATIONS = (Class<? extends Annotation>[])
            new Class<?>[]{
                    ManyToMany.class,
                    OneToMany.class
            };

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

    protected boolean shouldSer(final PropertyWriter writer, final JsonGenerator jgen) {
        String pathToTest = IncludePathsUtils.getPathToTest(writer, jgen);

        AnnotatedMember member = writer.getMember();

        if (member == null) {
            return true;
        }

        if (!member.hasOneOf(TO_MANY_ANNOTATIONS)) {
            return true;
        }


        if (CollectionUtils.isEmpty(IncludePathsHolder.getIncludedPaths())) {
            return false;
        }

        return IncludePathsHolder.getIncludedWithSubPaths().stream()
                .anyMatch(pathToTest::equals);
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
