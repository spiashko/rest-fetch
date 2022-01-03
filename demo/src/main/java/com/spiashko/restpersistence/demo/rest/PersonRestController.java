package com.spiashko.restpersistence.demo.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.restpersistence.demo.crudbase.View;
import com.spiashko.restpersistence.demo.person.Person;
import com.spiashko.restpersistence.demo.person.PersonCreationService;
import com.spiashko.restpersistence.demo.person.PersonSearchService;
import com.spiashko.restpersistence.rfetch.annotation.RfetchSpec;
import com.spiashko.restpersistence.rsqlspec.annotation.RsqlSpec;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@RestController
@RequestMapping("/persons")
public class PersonRestController {

    private final PersonCreationService creationService;
    private final PersonSearchService searchService;

    @JsonView(View.Retrieve.class)
    @PostMapping
    public Person create(@JsonView(View.PersonCreate.class) @RequestBody Person entityToCreate) {
        Person result = creationService.create(entityToCreate);
        return result;
    }

    @JsonView(View.Retrieve.class)
    @GetMapping
    public List<Person> findAll(
            @RfetchSpec Specification<Person> rFetchSpec,
            @RsqlSpec Specification<Person> rSqlSpec
    ) {

        val spec = Stream.of(rFetchSpec, rSqlSpec)
                .reduce(Specification.where(null),
                        Specification::and);
        List<Person> result = searchService.findAll(spec);
        return result;
    }

}
