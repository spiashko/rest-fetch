package com.spiashko.restpersistence.rsqlspec.annotation.security;

import com.spiashko.restpersistence.rsqlspec.annotation.RsqlValueInterceptor;
import com.spiashko.restpersistence.security.JsonViewSecurityInterceptor;
import io.github.perplexhub.rsql.RSQLCommonSupport;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;

import java.util.ArrayList;

@RequiredArgsConstructor
public class RsqlJsonViewSecurityInterceptor implements RsqlValueInterceptor {

    private final JsonViewSecurityInterceptor interceptor;

    @SneakyThrows
    @Override
    public void intercept(String rsqlString, MethodParameter parameter) {
        ArrayList<String> effectedPaths = new ArrayList<>(RSQLCommonSupport.toComplexMultiValueMap(rsqlString).keySet());
        interceptor.intercept(effectedPaths, parameter);
    }
}
