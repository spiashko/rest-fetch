package com.spiashko.restpersistence.repo;

import com.spiashko.restpersistence.fetch.FetchRelationsTemplate;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Configurable
@Transactional(readOnly = true)
public class ExtendedRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements ExtendedRepository<T, ID> {

    public ExtendedRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                                  EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    public List<T> findAll(List<String> includePaths, Specification<T> spec, Sort sort) {

        if (CollectionUtils.isEmpty(includePaths)) {
            return findAll(spec, sort);
        }

        return FetchRelationsTemplate.executeAndEnrichList(includePaths,
                this,
                r -> r.findAll(spec, sort));
    }

    @Override
    public Page<T> findAll(List<String> includePaths, Specification<T> spec, Pageable pageable) {
        if (CollectionUtils.isEmpty(includePaths)) {
            return findAll(spec, pageable);
        }

        return FetchRelationsTemplate.executeAndEnrichPage(includePaths,
                this,
                r -> r.findAll(spec, pageable));
    }

    @Override
    public Optional<T> findOne(List<String> includePaths, Specification<T> spec) {
        if (CollectionUtils.isEmpty(includePaths)) {
            return findOne(spec);
        }

        return FetchRelationsTemplate.executeAndEnrichOne(includePaths,
                this,
                r -> r.findOne(spec));

    }

}
