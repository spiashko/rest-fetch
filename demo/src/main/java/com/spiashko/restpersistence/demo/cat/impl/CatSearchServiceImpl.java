package com.spiashko.restpersistence.demo.cat.impl;

import com.spiashko.restpersistence.demo.cat.Cat;
import com.spiashko.restpersistence.demo.cat.CatSearchService;
import com.spiashko.restpersistence.demo.crudbase.BaseSearchServiceImpl;
import org.springframework.stereotype.Service;

@Service
class CatSearchServiceImpl
        extends BaseSearchServiceImpl<Cat, CatRepository>
        implements CatSearchService {

    public CatSearchServiceImpl(
            CatRepository repository) {
        super(repository);
    }
}
