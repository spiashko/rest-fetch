package com.spiashko.restpersistence.fetch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

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

        R result = actualOperation.get();
        Collection<T> entities = extractor.apply(result);

        if (CollectionUtils.isEmpty(entities)) {
            log.debug("nothing to enrich");
            return result;
        }

        for (String includedPath : includePaths) {
            PropertyPath path = PropertyPath.from(includedPath, domainClass);

            Collection currentCollection = entities;
            Class currentClass = domainClass;
            while (path != null) {
                Collection finaCurrentCollection = currentCollection;
                PropertyPath finalPath = path;

                Specification<Object> limitedByCollectionSpec = (root, query, builder) -> root.in(finaCurrentCollection);

                Specification<Object> fetchSpec = (root, query, builder) -> {
                    FetchParent<Object, Object> f = root.fetch(finalPath.getSegment(), JoinType.LEFT);
                    Join<Object, Object> join = (Join<Object, Object>) f;
                    query.distinct(true);
                    return join.getOn();
                };

                Collection enrichedCollection = getQuery(fetchSpec.and(limitedByCollectionSpec), currentClass).getResultList();
                Collection currentCollectionCandidate = new ArrayList<Object>();
                for (Object e : enrichedCollection) {
                    Field field = FieldUtils.getField(e.getClass(), finalPath.getSegment(), true);
                    Object nestedObject = ReflectionUtils.getField(field, e);
                    if (nestedObject instanceof Collection) {
                        currentCollectionCandidate.addAll((Collection) nestedObject);
                        //unwrap collection
                    } else {
                        currentCollectionCandidate.add(nestedObject);
                    }
                }

                currentCollection = currentCollectionCandidate;

                currentClass = path.getType();
                path = path.next();
            }
        }

        return result;
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
