package com.spiashko.rfetch.demo.crudbase.repository;


import com.spiashko.rfetch.demo.crudbase.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity>
        extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {
}
