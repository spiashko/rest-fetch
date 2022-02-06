package com.spiashko.jpafetch.demo.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.jpafetch.demo.crudbase.View;
import com.spiashko.jpafetch.demo.person.Person;
import com.spiashko.jpafetch.demo.person.PersonRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/persons")
public class PersonRestController {

    private final PersonRepository repository;

    @PreAuthorize("@jsonViewSecurityInterceptor.intercept(#includePaths, " +
            "T(com.spiashko.jpafetch.demo.person.Person), " +
            "T(com.spiashko.jpafetch.demo.crudbase.View$Retrieve))")
    @JsonView(View.Retrieve.class)
    @GetMapping
    public List<Person> findAll(
            @RequestParam(value = "filter", required = false) String rsqlFilter,
            @RequestParam(value = "include", required = false) List<String> includePaths
    ) {
        List<Person> result = repository.findAll(includePaths, RSQLJPASupport.rsql(rsqlFilter));
        return result;
    }

}
