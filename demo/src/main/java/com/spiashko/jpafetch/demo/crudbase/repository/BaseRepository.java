package com.spiashko.jpafetch.demo.crudbase.repository;


import com.spiashko.jpafetch.demo.crudbase.entity.BaseEntity;
import com.spiashko.jpafetch.repo.FetchSmartRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity>
        extends FetchSmartRepository<T, UUID>, JpaSpecificationExecutor<T> {
}