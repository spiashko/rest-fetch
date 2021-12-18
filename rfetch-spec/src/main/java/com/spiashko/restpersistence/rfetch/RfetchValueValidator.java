package com.spiashko.restpersistence.rfetch;

import org.springframework.core.MethodParameter;

import java.util.List;

public interface RfetchValueValidator {

    void validate(List<String> includedPaths, MethodParameter parameter);

}
