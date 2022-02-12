package com.spiashko.jpafetch.demo.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.jpafetch.demo.crudbase.View;
import com.spiashko.jpafetch.demo.person.Person;
import com.spiashko.jpafetch.demo.person.PersonRepository;
import com.spiashko.jpafetch.fetch.FetchSmartTemplate;
import com.spiashko.jpafetch.jacksonjpa.selfrefresolution.core.IncludePathsHolder;
import com.spiashko.jpafetch.security.JsonViewSecurityInterceptor;
import io.github.perplexhub.rsql.RSQLCommonSupport;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/persons")
public class PersonRestController {

    private final TransactionTemplate transactionTemplate;
    private final FetchSmartTemplate fetchSmartTemplate;
    private final PersonRepository repository;
    private final JsonViewSecurityInterceptor interceptor;

    @PreAuthorize("@jsonViewSecurityInterceptor.intercept(#includePaths, " +
            "T(com.spiashko.jpafetch.demo.person.Person), " +
            "T(com.spiashko.jpafetch.demo.crudbase.View$Retrieve))")
    @JsonView(View.Retrieve.class)
    @GetMapping
    public List<Person> findAll(
            @RequestParam(value = "filter", required = false) String rsqlFilter,
            @RequestParam(value = "include", required = false) List<String> includePaths
    ) {
        //TODO: move it to AOP (use annotation for include RequestParam)
        ArrayList<String> effectedPaths = new ArrayList<>(RSQLCommonSupport.toComplexMultiValueMap(rsqlFilter).keySet());
        interceptor.intercept(effectedPaths, Person.class, View.Retrieve.class);

        //TODO: move it to AOP
        IncludePathsHolder.setIncludedPaths(includePaths);

        return transactionTemplate.execute(s -> {
            List<Person> result = repository.findAll(RSQLJPASupport.rsql(rsqlFilter));
            fetchSmartTemplate.enrichList(includePaths, Person.class, result);
            return result;
        });
    }

}
