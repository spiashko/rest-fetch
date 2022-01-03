package com.spiashko.restpersistence.rfetch.annotation;

import org.springframework.core.MethodParameter;

import java.util.List;

public interface RfetchValueInterceptor {

    void intercept(List<String> includedPaths, MethodParameter parameter);

}
