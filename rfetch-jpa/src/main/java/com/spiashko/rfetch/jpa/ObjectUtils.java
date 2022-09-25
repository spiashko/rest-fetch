package com.spiashko.rfetch.jpa;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.Hibernate;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ObjectUtils {

    @SuppressWarnings("unchecked")
    public static Set<Object> extractNestedObjects(Collection<?> entities, String childNodeName) {
        Set<Object> nestedObjects = new HashSet<>();
        for (Object enrichedEntity : entities) {
            Object e = Hibernate.unproxy(enrichedEntity); // it should be already initialized
            Field field = FieldUtils.getField(e.getClass(), childNodeName, true);
            Object nestedObject = ReflectionUtils.getField(field, e);
            if (nestedObject instanceof Collection) {
                nestedObjects.addAll((Collection<Object>) nestedObject);
            } else {
                nestedObjects.add(nestedObject);
            }
        }
        return nestedObjects;
    }

}
