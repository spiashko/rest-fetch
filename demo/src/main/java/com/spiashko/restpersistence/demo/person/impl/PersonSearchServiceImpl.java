package com.spiashko.restpersistence.demo.person.impl;

import com.spiashko.restpersistence.demo.crudbase.BaseSearchServiceImpl;
import com.spiashko.restpersistence.demo.person.Person;
import com.spiashko.restpersistence.demo.person.PersonSearchService;
import org.springframework.stereotype.Service;

@Service
class PersonSearchServiceImpl
        extends BaseSearchServiceImpl<Person, PersonRepository>
        implements PersonSearchService {

    public PersonSearchServiceImpl(PersonRepository repository) {
        super(repository);
    }
}
