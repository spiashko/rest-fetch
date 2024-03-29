package com.spiashko.rfetch.security;

import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.rfetch.parser.RfetchNode;
import com.spiashko.rfetch.parser.RfetchSupport;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JsonViewSecurityInterceptor {

    @SneakyThrows
    public boolean intercept(RfetchNode root, Class<?> entityClass, Class<?> responseJsonView) {
        List<String> effectedPaths = RfetchSupport.effectedPaths(root);
        return intercept(effectedPaths, entityClass, responseJsonView);
    }

    @SneakyThrows
    public boolean intercept(List<String> effectedPaths, Class<?> entityClass, Class<?> responseJsonView) {

        if (effectedPaths == null) {
            return true;
        }

        for (String includedPath : effectedPaths) {
            List<String> pathParts = Stream.of(includedPath.split("\\."))
                    .collect(Collectors.toList());

            Class<?> currentClass = entityClass;
            for (String pathPart : pathParts) {
                Field endField = currentClass.getDeclaredField(pathPart);

                List<Class<?>> fieldJsonViews = Optional.of(endField)
                        .map(f -> f.getAnnotation(JsonView.class))
                        .map(JsonView::value)
                        .map(Arrays::asList)
                        .orElseThrow(() -> new RuntimeException("failed to identify JsonView classes for field " + includedPath));

                if (!fieldJsonViews.contains(responseJsonView)) {
                    throw new NoSuchFieldException(includedPath);
                }

                Type candidate = endField.getGenericType();
                if (candidate instanceof ParameterizedType) {
                    currentClass = (Class<?>) ((ParameterizedType) candidate).getActualTypeArguments()[0];
                } else {
                    currentClass = (Class<?>) candidate;
                }
            }
        }

        return true;
    }
}
