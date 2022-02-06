package com.spiashko.restpersistence.demo.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.restpersistence.demo.cat.Cat;
import com.spiashko.restpersistence.demo.cat.CatRepository;
import com.spiashko.restpersistence.demo.crudbase.View;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CatRestController {

    private final CatRepository repository;

    @JsonView(View.Retrieve.class)
    @PostMapping("/cats")
    public Cat create(@JsonView(View.CatCreate.class) @RequestBody Cat entityToCreate) {
        Cat result = repository.save(entityToCreate);
        return result;
    }

    @JsonView(View.Retrieve.class)
    @GetMapping("/cats")
    public List<Cat> findAll(
            @RequestParam(value = "filter", required = false) String rsqlFilter,
            @RequestParam(value = "include", required = false) List<String> includePaths
    ) {
        List<Cat> result = repository.findAll(includePaths, RSQLJPASupport.rsql(rsqlFilter));
        return result;
    }

}
