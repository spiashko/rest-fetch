package com.spiashko.restpersistence.demo.cat.impl;

import com.spiashko.restpersistence.demo.cat.Cat;
import com.spiashko.restpersistence.demo.cat.CatCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
class CatCreationServiceImpl implements CatCreationService {

    private final CatRepository repository;

    @Transactional
    @Override
    public Cat create(Cat entityToCreate) {
        Cat entity = repository.save(entityToCreate);
        return entity;
    }

}
