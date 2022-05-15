package com.spiashko.rfetch.demo.rest;

import com.spiashko.rfetch.demo.selfrefresolution.core.IncludePathsHolder;
import com.spiashko.rfetch.parser.RfetchSupport;
import com.spiashko.rfetch.security.JsonViewSecurityInterceptor;
import io.github.perplexhub.rsql.RSQLCommonSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class BeforeRequestActionsExecutor {

    private final JsonViewSecurityInterceptor interceptor;

    public <T> void execute(String rsql, String rfetch, Class<T> entity, Class<?> jsonView) {
        //rsql
        List<String> rsqlEffectedPaths = new ArrayList<>(RSQLCommonSupport.toComplexMultiValueMap(rsql).keySet());
        interceptor.intercept(rsqlEffectedPaths, entity, jsonView);

        //rfetch
        List<String> rfetchEffectedPaths = RfetchSupport.effectedPaths(rfetch, entity);
        interceptor.intercept(rfetchEffectedPaths, entity, jsonView);

        IncludePathsHolder.setIncludedPaths(rfetchEffectedPaths);
    }

}
