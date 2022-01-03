package com.spiashko.restpersistence.rfetch.annotation;

import com.spiashko.restpersistence.rfetch.core.RfetchSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class RfetchSpecArgumentResolver implements HandlerMethodArgumentResolver {

    private final RfetchSupport rfetchSupport;
    private final List<RfetchValueCustomizer> valueCustomizers;
    private final List<RfetchValueInterceptor> valueInterceptors;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();

        return paramType.isInterface() &&
                Specification.class.isAssignableFrom(paramType) &&
                isAnnotated(parameter);
    }

    private boolean isAnnotated(MethodParameter methodParameter) {
        for (Annotation annotation : methodParameter.getParameterAnnotations()) {
            if (RfetchSpec.class.equals(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        RfetchSpec rfetchSpecAnnotation = parameter.getParameterAnnotation(RfetchSpec.class);
        String paramName = Objects.requireNonNull(rfetchSpecAnnotation).requestParamName();

        String value = webRequest.getParameter(paramName);

        List<String> includedPaths = value == null ?
                new ArrayList<>() :
                Arrays.asList(value.split(";"));

        for (RfetchValueCustomizer customizer : valueCustomizers) {
            includedPaths = customizer.customize(includedPaths);
        }

        for (RfetchValueInterceptor interceptor : valueInterceptors) {
            interceptor.intercept(includedPaths, parameter);
        }

        Specification<Object> rfetchSpec = rfetchSupport.toSpecification(includedPaths);

        return rfetchSpec;
    }

}
