package com.spiashko.restpersistence.demo.person.impl;

import com.spiashko.restpersistence.demo.person.Person;
import com.spiashko.restpersistence.demo.person.PersonCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class PersonCreationServiceImpl implements PersonCreationService {

    private final PersonRepository repository;

    @Override
    public Person create(Person entityToCreate) {
        Person entity = repository.save(entityToCreate);
        return entity;
    }

}
