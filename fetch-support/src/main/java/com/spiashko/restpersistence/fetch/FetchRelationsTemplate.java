package com.spiashko.restpersistence.fetch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@SuppressWarnings("unchecked")
@Slf4j
public class FetchRelationsTemplate {

    private final EntityManager em;

    public <T> List<T> executeAndEnrichList(List<String> includePaths,
                                            Class<T> domainClass,
                                            JpaSpecificationExecutor<T> repository,
                                            Function<JpaSpecificationExecutor<T>, List<T>> actualOperation) {
        return executeAndEnrich(includePaths,
                domainClass,
                () -> actualOperation.apply(repository),
                list -> list
        );
    }

    public <T> Page<T> executeAndEnrichPage(List<String> includePaths,
                                            Class<T> domainClass,
                                            JpaSpecificationExecutor<T> repository,
                                            Function<JpaSpecificationExecutor<T>, Page<T>> actualOperation) {
        return executeAndEnrich(includePaths,
                domainClass,
                () -> actualOperation.apply(repository),
                Slice::getContent
        );
    }

    public <T> Optional<T> executeAndEnrichOne(List<String> includePaths,
                                               Class<T> domainClass,
                                               JpaSpecificationExecutor<T> repository,
                                               Function<JpaSpecificationExecutor<T>, Optional<T>> actualOperation) {
        return executeAndEnrich(includePaths,
                domainClass,
                () -> actualOperation.apply(repository),
                one -> one.map(Collections::singletonList).orElse(Collections.emptyList())
        );
    }

    public <R, T> R executeAndEnrich(List<String> includePaths,
                                     Class<T> domainClass,
                                     Supplier<R> actualOperation,
                                     Function<R, Collection<T>> extractor) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new RuntimeException("method must be executed within transaction");
        }

        List<Specification<Object>> includeSpecifications = includePaths.stream()
                .map(this::buildFetchSpec)
                .collect(Collectors.toList());

        R result = actualOperation.get();
        Collection<T> entities = extractor.apply(result);

        if (CollectionUtils.isEmpty(entities)) {
            log.debug("nothing to enrich");
            return result;
        }

        Specification<Object> limitedByCollection = (root, query, builder) -> root.in(entities);

        for (Specification<Object> spec : includeSpecifications) {
            Specification joinedSpec = spec.and(limitedByCollection);
            getQuery(joinedSpec, domainClass).getResultList();
        }

        return result;
    }

    private Specification<Object> buildFetchSpec(String attributePath) {
        return (root, query, builder) -> {
            PropertyPath path = PropertyPath.from(attributePath, root.getJavaType());
            FetchParent<Object, Object> f = traversePathWithFetch(root, path);
            Join<Object, Object> join = (Join<Object, Object>) f;

            query.distinct(true);

            return join.getOn();
        };
    }

    private FetchParent<Object, Object> traversePathWithFetch(FetchParent<?, ?> root, PropertyPath path) {
        FetchParent<Object, Object> result = root.fetch(path.getSegment(), JoinType.LEFT);
        return path.hasNext() ? traversePathWithFetch(result, Objects.requireNonNull(path.next())) : result;
    }

    protected <T> TypedQuery<T> getQuery(@Nullable Specification<T> spec, Class<T> domainClass) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(domainClass);

        Root<T> root = applySpecificationToCriteria(spec, domainClass, query);
        query.select(root);

        return em.createQuery(query);
    }

    private <S, T> Root<T> applySpecificationToCriteria(@Nullable Specification<T> spec, Class<T> domainClass,
                                                        CriteriaQuery<S> query) {

        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(query, "CriteriaQuery must not be null!");

        Root<T> root = query.from(domainClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }
}
