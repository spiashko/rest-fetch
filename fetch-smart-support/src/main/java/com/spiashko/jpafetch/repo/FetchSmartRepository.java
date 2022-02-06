package com.spiashko.jpafetch.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

import javax.persistence.QueryHint;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface FetchSmartRepository<T, ID extends Serializable>
        extends JpaRepository<T, ID> {

    default List<T> findAll(@Nullable List<String> includePaths) {
        return findAll(includePaths, null, Sort.unsorted());
    }

    default List<T> findAll(@Nullable List<String> includePaths, Sort sort) {
        return findAll(includePaths, null, sort);
    }

    default List<T> findAll(@Nullable List<String> includePaths, Specification<T> spec) {
        return findAll(includePaths, spec, Sort.unsorted());
    }

    @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false"))
    List<T> findAll(@Nullable List<String> includePaths, Specification<T> spec, Sort sort);

    default Page<T> findAll(@Nullable List<String> includePaths, Pageable pageable) {
        return findAll(includePaths, null, pageable);
    }

    @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false"))
    Page<T> findAll(@Nullable List<String> includePaths, Specification<T> spec, Pageable pageable);

    default Optional<T> findOne(@Nullable List<String> includePaths) {
        return findOne(includePaths, null);
    }

    @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false"))
    Optional<T> findOne(List<String> includePaths, Specification<T> spec);

}
