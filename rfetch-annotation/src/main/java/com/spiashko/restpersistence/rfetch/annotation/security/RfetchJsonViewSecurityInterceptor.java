package com.spiashko.restpersistence.rfetch.annotation.security;

import com.spiashko.restpersistence.rfetch.annotation.RfetchValueInterceptor;
import com.spiashko.restpersistence.security.JsonViewSecurityInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;

import java.util.List;

@RequiredArgsConstructor
public class RfetchJsonViewSecurityInterceptor implements RfetchValueInterceptor {

    private final JsonViewSecurityInterceptor interceptor;

    @SneakyThrows
    @Override
    public void intercept(List<String> includedPaths, MethodParameter parameter) {
        interceptor.intercept(includedPaths, parameter);
    }
}
