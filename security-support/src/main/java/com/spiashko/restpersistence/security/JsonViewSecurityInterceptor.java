package com.spiashko.restpersistence.security;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;

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
    public void intercept(List<String> effectedPaths, MethodParameter parameter) {

        Class<?> responseJsonView = Optional.ofNullable(parameter.getMethodAnnotation(JsonView.class))
                .map(JsonView::value)
                .map(Arrays::asList)
                .filter(l -> l.size() == 1)
                .map(l -> l.get(0))
                .orElseThrow(() -> new RuntimeException("JsonView annotation must be present and contain exactly 1 view"));

        Class<?> entityClass = (Class<?>) ((ParameterizedType) parameter.getGenericParameterType()).getActualTypeArguments()[0];


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
    }
}
