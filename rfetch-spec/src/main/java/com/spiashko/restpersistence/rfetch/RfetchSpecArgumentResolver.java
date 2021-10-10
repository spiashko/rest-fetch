package com.spiashko.restpersistence.rfetch;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public class RfetchSpecArgumentResolver implements HandlerMethodArgumentResolver {

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

        if (value == null) {
            return null;
        }

        Specification<Object> rfetchSpec = Arrays.stream(value.split(";"))
                .reduce(Specification.where(null),
                        (objectSpecification, s) -> objectSpecification.and(buildSpec(s)),
                        Specification::and);

        return rfetchSpec;
    }

    private Specification<Object> buildSpec(String attributePath) {
        return (Specification<Object>) (root, query, builder) -> {
            PropertyPath path = PropertyPath.from(attributePath, root.getJavaType());
            FetchParent<Object, Object> f = traversePath(root, path);
            Join join = (Join) f;

            query.distinct(true);

            return join.getOn();
        };
    }

    private FetchParent<Object, Object> traversePath(FetchParent<?, ?> root, PropertyPath path) {
        FetchParent<Object, Object> result = root.fetch(path.getSegment(), JoinType.LEFT);
        return path.hasNext() ? traversePath(result, Objects.requireNonNull(path.next())) : result;
    }

}