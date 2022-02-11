package com.spiashko.jpafetch.repo;

import com.spiashko.jpafetch.fetch.FetchSmartTemplate;
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
public class FetchSmartRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements FetchSmartRepository<T, ID> {

    private final FetchSmartTemplate fetchSmartTemplate;

    public FetchSmartRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                                    EntityManager entityManager) {
        super(entityInformation, entityManager);
        fetchSmartTemplate = new FetchSmartTemplate(entityManager);
    }

    @Override
    public List<T> findAll(List<String> includePaths, Specification<T> spec, Sort sort) {
        List<T> all = findAll(spec, sort);
        if (CollectionUtils.isEmpty(includePaths)) {
            return all;
        }
        fetchSmartTemplate.enrichList(includePaths, getDomainClass(), all);
        return all;
    }

    @Override
    public Page<T> findAll(List<String> includePaths, Specification<T> spec, Pageable pageable) {
        Page<T> all = findAll(spec, pageable);
        if (CollectionUtils.isEmpty(includePaths)) {
            return all;
        }
        fetchSmartTemplate.enrichPage(includePaths, getDomainClass(), all);
        return all;
    }

    @Override
    public Optional<T> findOne(List<String> includePaths, Specification<T> spec) {
        Optional<T> one = findOne(spec);
        if (CollectionUtils.isEmpty(includePaths)) {
            return one;
        }
        fetchSmartTemplate.enrichOne(includePaths, getDomainClass(), one);
        return one;
    }

}
