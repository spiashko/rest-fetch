package com.spiashko.restpersistence.demo.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.restpersistence.demo.crudbase.View;
import com.spiashko.restpersistence.demo.person.Person;
import com.spiashko.restpersistence.demo.person.PersonCreationService;
import com.spiashko.restpersistence.demo.person.PersonSearchService;
import com.spiashko.restpersistence.rfetch.RfetchSpec;
import com.spiashko.restpersistence.rsqlspec.RsqlSpec;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/persons")
public class PersonRestController {

    private final PersonCreationService creationService;
    private final PersonSearchService searchService;

    @JsonView(View.Retrieve.class)
    @PostMapping
    public Person create(@JsonView(View.Create.class) @RequestBody Person entityToCreate) {
        Person result = creationService.create(entityToCreate);
        return result;
    }

    @JsonView(View.Retrieve.class)
    @GetMapping
    public List<Person> findAll(
            @Parameter(hidden = true)
            @RfetchSpec Specification<Person> rFetchSpec,
            @RsqlSpec Specification<Person> rSqlSpec
    ) {
        val spec = Specification
                .where(rFetchSpec)
                .and(rSqlSpec);
        List<Person> result = searchService.findAll(spec);
        return result;
    }

}
