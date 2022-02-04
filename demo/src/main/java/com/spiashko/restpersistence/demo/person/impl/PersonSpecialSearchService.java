package com.spiashko.restpersistence.demo.person.impl;

import com.spiashko.restpersistence.demo.person.Person;
import com.spiashko.restpersistence.rfetch.core.RfetchSupport;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PersonSpecialSearchService {

    private final PersonRepository repository;

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Person> bestSearch(String rsql, List<String> includedPathse) {

        Specification<Person> filterSpec = RSQLJPASupport.toSpecification(rsql, true);
        List<Specification<Object>> includeSpecifications = (new RfetchSupport()).toSpecificationList(includedPathse);

        List<Person> filteredPeople = repository.findAll(filterSpec);

        Specification<Object> kek = (root, query, builder) -> {
            return root.in(filteredPeople);
        };

        List<Person> people = filteredPeople;
        for (Specification<Object> spec : includeSpecifications) {
            Specification joinedSpec = spec.and(kek);
            people = repository.findAll(joinedSpec);
        }

        return people;
    }

}
