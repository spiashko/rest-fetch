package com.spiashko.restpersistence.rsqlspec.annotation;

import org.springframework.core.MethodParameter;

public interface RsqlValueInterceptor {

    void intercept(String rsqlString, MethodParameter parameter);

}
