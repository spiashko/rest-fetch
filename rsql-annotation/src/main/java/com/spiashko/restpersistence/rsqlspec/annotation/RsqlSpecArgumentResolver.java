package com.spiashko.restpersistence.rsqlspec.annotation;

import io.github.perplexhub.rsql.RSQLCommonSupport;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class RsqlSpecArgumentResolver implements HandlerMethodArgumentResolver {

    private final List<RsqlValueCustomizer> valueCustomizers;
    private final List<RsqlValueInterceptor> valueInterceptors;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();

        return paramType.isInterface() &&
                Specification.class.isAssignableFrom(paramType) &&
                isAnnotated(parameter);
    }

    private boolean isAnnotated(MethodParameter methodParameter) {
        for (Annotation annotation : methodParameter.getParameterAnnotations()) {
            if (RsqlSpec.class.equals(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        RsqlSpec rsqlSpecAnnotation = parameter.getParameterAnnotation(RsqlSpec.class);
        String paramName = Objects.requireNonNull(rsqlSpecAnnotation).requestParamName();
        AndPathVarEq[] andPathVarEqs = rsqlSpecAnnotation.extensionFromPath();

        String rsqlString = webRequest.getParameter(paramName);

        for (RsqlValueCustomizer customizer : valueCustomizers) {
            rsqlString = customizer.customize(rsqlString);
        }

        for (RsqlValueInterceptor interceptor : valueInterceptors) {
            interceptor.intercept(rsqlString, parameter);
        }

        Specification<Object> rsqlSpec = RSQLJPASupport.toSpecification(rsqlString, true);

        if (andPathVarEqs.length == 0) {
            return rsqlSpec;
        }

        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        Map<String, String> uriTemplateVariables =
                (Map<String, String>) Objects.requireNonNull(httpServletRequest)
                        .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        Specification<Object> fullSpec = rsqlSpec;
        for (AndPathVarEq andPathVarEq : andPathVarEqs) {
            String attributePath = andPathVarEq.attributePath();
            String pathVar = andPathVarEq.pathVar();

            String pathVarValue = uriTemplateVariables.get(pathVar);
            fullSpec = buildEqSpec(pathVarValue, attributePath).and(fullSpec);
        }

        return fullSpec;
    }

    private Specification<Object> buildEqSpec(Object pathVarValue, String attributePath) {
        return (Root<Object> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            PropertyPath path = PropertyPath.from(attributePath, root.getJavaType());
            return criteriaBuilder.equal(traversePath(root, path), pathVarValue);
        };
    }

    private Expression<Object> traversePath(Path<?> root, PropertyPath path) {
        Path<Object> result = root.get(path.getSegment());
        return path.hasNext() ? traversePath(result, Objects.requireNonNull(path.next())) : result;
    }

}
