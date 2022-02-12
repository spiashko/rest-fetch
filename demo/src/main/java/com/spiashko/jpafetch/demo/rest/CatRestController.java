package com.spiashko.jpafetch.demo.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.jpafetch.demo.cat.Cat;
import com.spiashko.jpafetch.demo.cat.CatRepository;
import com.spiashko.jpafetch.demo.crudbase.View;
import com.spiashko.jpafetch.fetch.FetchSmartTemplate;
import com.spiashko.jpafetch.jacksonjpa.selfrefresolution.core.IncludePathsHolder;
import com.spiashko.jpafetch.security.JsonViewSecurityInterceptor;
import io.github.perplexhub.rsql.RSQLCommonSupport;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class CatRestController {

    private final TransactionTemplate transactionTemplate;
    private final FetchSmartTemplate fetchSmartTemplate;
    private final CatRepository repository;
    private final JsonViewSecurityInterceptor interceptor;

    @PreAuthorize("@jsonViewSecurityInterceptor.intercept(#includePaths, " +
            "T(com.spiashko.jpafetch.demo.cat.Cat), " +
            "T(com.spiashko.jpafetch.demo.crudbase.View$Retrieve))")
    @JsonView(View.Retrieve.class)
    @GetMapping("/cats")
    public List<Cat> findAll(
            @RequestParam(value = "filter", required = false) String rsqlFilter,
            @RequestParam(value = "include", required = false) List<String> includePaths
    ) {
        ArrayList<String> effectedPaths = new ArrayList<>(RSQLCommonSupport.toComplexMultiValueMap(rsqlFilter).keySet());
        interceptor.intercept(effectedPaths, Cat.class, View.Retrieve.class);

        IncludePathsHolder.setIncludedPaths(includePaths);

        return transactionTemplate.execute(s -> {
            List<Cat> result = repository.findAll(RSQLJPASupport.rsql(rsqlFilter));
            fetchSmartTemplate.enrichList(includePaths, Cat.class, result);
            return result;
        });
    }


}
