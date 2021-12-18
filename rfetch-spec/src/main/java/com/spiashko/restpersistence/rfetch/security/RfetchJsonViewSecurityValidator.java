package com.spiashko.restpersistence.rfetch.security;

import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.restpersistence.rfetch.RfetchValueValidator;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RfetchJsonViewSecurityValidator implements RfetchValueValidator {

    @SneakyThrows
    @Override
    public void validate(List<String> includedPaths, MethodParameter parameter) {

        Class<?> responseJsonView = Optional.ofNullable(parameter.getMethodAnnotation(JsonView.class))
                .map(JsonView::value)
                .map(Arrays::asList)
                .filter(l -> l.size() == 1)
                .map(l -> l.get(0))
                .orElseThrow(() -> new RuntimeException("JsonView annotation must be present and contain exactly 1 view"));

        Class<?> entityClass = (Class<?>) ((ParameterizedType) parameter.getGenericParameterType()).getActualTypeArguments()[0];


        for (String includedPath : includedPaths) {
            List<String> pathParts = Stream.of(includedPath.split("\\."))
                    .collect(Collectors.toList());

            Class<?> currentClass = entityClass;
            Field endField = null;
            for (String pathPart : pathParts) {
                endField = currentClass.getDeclaredField(pathPart);
                currentClass = endField.getType();
            }

            List<Class<?>> fieldJsonViews = Optional.ofNullable(endField)
                    .map(f -> f.getAnnotation(JsonView.class))
                    .map(JsonView::value)
                    .map(Arrays::asList)
                    .orElseThrow(() -> new RuntimeException("failed to identify JsonView classes for field " + includedPath));

            if (!fieldJsonViews.contains(responseJsonView)) {
                throw new NoSuchFieldException(includedPath);
            }

        }

        System.out.println();
    }
}
