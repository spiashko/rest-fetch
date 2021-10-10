package com.spiashko.restpersistence.demo.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.restpersistence.demo.cat.Cat;
import com.spiashko.restpersistence.demo.cat.CatCreationService;
import com.spiashko.restpersistence.demo.cat.CatSearchService;
import com.spiashko.restpersistence.demo.crudbase.View;
import com.spiashko.restpersistence.rfetch.RfetchSpec;
import com.spiashko.restpersistence.rsqlspec.RsqlSpec;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CatRestController {

    private final CatCreationService creationService;
    private final CatSearchService searchService;

    @JsonView(View.Retrieve.class)
    @PostMapping("/cats")
    public Cat create(@JsonView(View.Create.class) @RequestBody Cat entityToCreate) {
        Cat result = creationService.create(entityToCreate);
        return result;
    }

    @JsonView(View.Retrieve.class)
    @GetMapping("/cats")
    public List<Cat> findAll(
            @RfetchSpec Specification<Cat> rFetchSpec,
            @RsqlSpec Specification<Cat> rSqlSpec
    ) {
        val spec = Specification
                .where(rFetchSpec)
                .and(rSqlSpec);
        List<Cat> result = searchService.findAll(spec);
        return result;
    }

}
