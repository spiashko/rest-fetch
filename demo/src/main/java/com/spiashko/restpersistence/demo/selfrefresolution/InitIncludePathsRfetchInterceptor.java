package com.spiashko.restpersistence.demo.selfrefresolution;

import com.spiashko.restpersistence.jacksonjpa.selfrefresolution.IncludePathsHolder;
import com.spiashko.restpersistence.rfetch.annotation.RfetchValueInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

import java.util.List;

//TODO: Add servlet filter to clean up
@Component
@RequiredArgsConstructor
public class InitIncludePathsRfetchInterceptor implements RfetchValueInterceptor {

    @SneakyThrows
    @Override
    public void intercept(List<String> includedPaths, MethodParameter parameter) {
        IncludePathsHolder.setIncludedPaths(includedPaths);
    }
}
