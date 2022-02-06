package com.spiashko.restpersistence.repo;

import com.spiashko.restpersistence.fetch.FetchRelationsTemplate;
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

@Transactional(readOnly = true)
public class ExtendedRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements ExtendedRepository<T, ID> {

    private final FetchRelationsTemplate fetchRelationsTemplate;

    public ExtendedRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                                  EntityManager entityManager) {
        super(entityInformation, entityManager);
        fetchRelationsTemplate = new FetchRelationsTemplate(entityManager);
    }

    @Override
    public List<T> findAll(List<String> includePaths, Specification<T> spec, Sort sort) {
        if (CollectionUtils.isEmpty(includePaths)) {
            return findAll(spec, sort);
        }
        return fetchRelationsTemplate.executeAndEnrichList(includePaths,
                getDomainClass(),
                this,
                r -> r.findAll(spec, sort));
    }

    @Override
    public Page<T> findAll(List<String> includePaths, Specification<T> spec, Pageable pageable) {
        if (CollectionUtils.isEmpty(includePaths)) {
            return findAll(spec, pageable);
        }
        return fetchRelationsTemplate.executeAndEnrichPage(includePaths,
                getDomainClass(),
                this,
                r -> r.findAll(spec, pageable));
    }

    @Override
    public Optional<T> findOne(List<String> includePaths, Specification<T> spec) {
        if (CollectionUtils.isEmpty(includePaths)) {
            return findOne(spec);
        }
        return fetchRelationsTemplate.executeAndEnrichOne(includePaths,
                getDomainClass(),
                this,
                r -> r.findOne(spec));
    }

}
