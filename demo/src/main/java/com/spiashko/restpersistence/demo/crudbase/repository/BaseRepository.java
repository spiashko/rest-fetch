package com.spiashko.restpersistence.demo.crudbase.repository;


import com.spiashko.restpersistence.demo.crudbase.entity.BaseEntity;
import com.spiashko.restpersistence.repo.ExtendedRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity>
        extends ExtendedRepository<T, UUID>, JpaSpecificationExecutor<T> {
}
