package com.spiashko.restpersistence.demo.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.restpersistence.demo.crudbase.View;
import com.spiashko.restpersistence.demo.person.Person;
import com.spiashko.restpersistence.demo.person.PersonRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/persons")
public class PersonRestController {

    private final PersonRepository repository;

    @JsonView(View.Retrieve.class)
    @PostMapping
    public Person create(@JsonView(View.PersonCreate.class) @RequestBody Person entityToCreate) {
        Person result = repository.save(entityToCreate);
        return result;
    }

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
