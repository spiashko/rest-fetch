package com.spiashko.restpersistence.demo.person.impl;

import com.spiashko.restpersistence.demo.dynamicfetch.FetchRelationsTemplate;
import com.spiashko.restpersistence.demo.person.Person;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PersonSpecialSearchService {

    private final PersonRepository repository;
    private final FetchRelationsTemplate fetchRelationsTemplate;

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Person> bestSearch(String rsql, List<String> includedPaths) {

        Specification<Person> filterSpec = RSQLJPASupport.toSpecification(rsql, true);

        List<Person> people = fetchRelationsTemplate.executeAndEnrich(
                includedPaths,
                repository,
                r -> r.findAll(filterSpec)
        );


        return people;
    }

}
