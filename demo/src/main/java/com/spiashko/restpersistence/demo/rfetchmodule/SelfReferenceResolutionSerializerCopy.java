//package com.spiashko.restpersistence.demo.rfetchmodule;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.core.JsonStreamContext;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
//import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
//import com.fasterxml.jackson.databind.ser.PropertyWriter;
//import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
//import com.spiashko.restpersistence.rfetch.RfetchPathsHolder;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.proxy.HibernateProxy;
//
//import javax.persistence.ManyToOne;
//import javax.persistence.OneToOne;
//import java.lang.annotation.Annotation;
//import java.util.HashMap;
//
//
//@Slf4j
//@RequiredArgsConstructor
//public class SelfReferenceResolutionSerializerCopy extends DelegatingSerializer {
//
//    @SuppressWarnings("unchecked")
//    private static Class<? extends Annotation>[] TO_ONE_ANNOTATIONS = (Class<? extends Annotation>[])
//            new Class<?>[]{
//                    ManyToOne.class,
//                    OneToOne.class
//            };
//
//    private final RfetchPathsHolder rfetchPathsHolder;
//
//    protected PropertySerializerMap _dynamicSerializers = PropertySerializerMap.emptyForProperties();
//
//    /**
//     * Gets the path to test.
//     *
//     * @param writer the writer
//     * @param jgen   the jgen
//     * @return the path to test
//     */
//    private String getPathToTest(final PropertyWriter writer, final JsonGenerator jgen) {
//        StringBuilder nestedPath = new StringBuilder();
//        nestedPath.append(writer.getName());
//        JsonStreamContext sc = jgen.getOutputContext();
//        if (sc != null) {
//            sc = sc.getParent();
//        }
//        while (sc != null) {
//            if (sc.getCurrentName() != null) {
//                if (nestedPath.length() > 0) {
//                    nestedPath.insert(0, ".");
//                }
//                nestedPath.insert(0, sc.getCurrentName());
//            }
//            sc = sc.getParent();
//        }
//        return nestedPath.toString();
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter#include(
//     * com.fasterxml.jackson.databind.ser. BeanPropertyWriter)
//     */
//    @Override
//    protected boolean include(final BeanPropertyWriter writer) {
//        throw new UnsupportedOperationException("Cannot call include without JsonGenerator");
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter#include(
//     * com.fasterxml.jackson.databind.ser. PropertyWriter)
//     */
//    @Override
//    protected boolean include(final PropertyWriter writer) {
//        throw new UnsupportedOperationException("Cannot call include without JsonGenerator");
//    }
//
//    /**
//     * Include.
//     *
//     * @param writer the writer
//     * @param jgen   the jgen
//     * @return true, if successful
//     */
//    protected boolean shouldSerAsUsual(final Object pojo, final PropertyWriter writer, final JsonGenerator jgen) {
//        String pathToTest = getPathToTest(writer, jgen);
//
//        AnnotatedMember member = writer.getMember();
//
//        if (member == null) {
//            return true;
//        }
//
//        if (!member.hasOneOf(TO_ONE_ANNOTATIONS)) {
//            return true;
//        }
//
//        try {
//            if (writer.getMember().getValue(pojo) instanceof HibernateProxy) {
//                return true;
//            }
//        } catch (IllegalArgumentException ex) {
//            return true;
//        }
//
//
//        if (rfetchPathsHolder.getIncludedPaths() == null) {
//            return false;
//        }
//
//        return rfetchPathsHolder.getIncludedPaths().stream().anyMatch(pathToTest::equals);
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter#
//     * serializeAsField(java.lang.Object,
//     * com.fasterxml.jackson.core.JsonGenerator,
//     * com.fasterxml.jackson.databind.SerializerProvider,
//     * com.fasterxml.jackson.databind.ser.PropertyWriter)
//     */
//    @Override
//    public void serializeAsField(final Object pojo, final JsonGenerator jgen, final SerializerProvider provider,
//                                 final PropertyWriter writer) throws Exception {
//
//        if (shouldSerAsUsual(pojo, writer, jgen)) {
//            writer.serializeAsField(pojo, jgen, provider);
//        } else { // ser only id
//
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("kek", "lol");
//
//            writer.serializeAsField(map, jgen, provider);
//        }
//    }
//}
