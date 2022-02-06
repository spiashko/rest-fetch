package com.spiashko.jpafetch.demo.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.jpafetch.demo.cat.Cat;
import com.spiashko.jpafetch.demo.cat.CatRepository;
import com.spiashko.jpafetch.demo.crudbase.View;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CatRestController {

    private final CatRepository repository;

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
