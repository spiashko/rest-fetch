package com.spiashko.jpafetch.fetch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
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

@RequiredArgsConstructor
@Slf4j
public class FetchSmartTemplate {

    private final EntityManager em;

    public <T> void enrichList(List<String> includePaths,
                               Class<T> domainClass,
                               List<T> entities) {
        enrich(
                includePaths,
                domainClass,
                entities
        );
    }

    public <T> void enrichPage(List<String> includePaths,
                               Class<T> domainClass,
                               Page<T> entitiesPage) {
        enrich(
                includePaths,
                domainClass,
                entitiesPage.getContent()
        );
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public <T> void enrichOne(List<String> includePaths,
                              Class<T> domainClass,
                              Optional<T> entity) {
        enrich(
                includePaths,
                domainClass,
                entity
                        .map(Collections::singletonList)
                        .orElse(Collections.emptyList())
        );
    }

    @SuppressWarnings("unchecked")
    public <T> void enrich(List<String> includePaths,
                           Class<T> domainClass,
                           Collection<T> entities) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new RuntimeException("method must be executed within transaction");
        }

        if (CollectionUtils.isEmpty(includePaths)) {
            log.debug("nothing to enrich");
            return;
        }

        if (CollectionUtils.isEmpty(entities)) {
            log.debug("no entities to enrich");
            return;
        }

        /* TODO:
         * add cache for situations like include=kittens.motherForKids,kittens.fatherForKids
         * or even make include like include=(kittens(motherForKids,fatherForKids),bestFriend)
         * an then visit recursively
         */

        for (String includedPath : includePaths) {
            PropertyPath path = PropertyPath.from(includedPath, domainClass);

            Collection<Object> currentCollection = (Collection<Object>) entities;
            Class<Object> currentClass = (Class<Object>) domainClass;
            while (path != null) {
                Collection<?> finaCurrentCollection = currentCollection;
                PropertyPath finalPath = path;

                Specification<Object> limitedByCollectionSpec = (root, query, builder) -> root.in(finaCurrentCollection);

                Specification<Object> fetchSpec = (root, query, builder) -> {
                    FetchParent<Object, Object> f = root.fetch(finalPath.getSegment(), JoinType.LEFT);
                    Join<Object, Object> join = (Join<Object, Object>) f;
                    query.distinct(true);
                    return join.getOn();
                };

                Collection<Object> enrichedCollection = getQuery(fetchSpec.and(limitedByCollectionSpec), currentClass).getResultList();
                Collection<Object> currentCollectionCandidate = new ArrayList<>();
                for (Object e : enrichedCollection) {
                    Field field = FieldUtils.getField(e.getClass(), finalPath.getSegment(), true);
                    Object nestedObject = ReflectionUtils.getField(field, e);
                    if (nestedObject instanceof Collection) {
                        currentCollectionCandidate.addAll((Collection<Object>) nestedObject);
                    } else {
                        currentCollectionCandidate.add(nestedObject);
                    }
                }

                currentCollection = currentCollectionCandidate;

                currentClass = (Class<Object>) path.getType();
                path = path.next();
            }
        }
    }

    private <T> TypedQuery<T> getQuery(@Nullable Specification<T> spec, Class<T> domainClass) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(domainClass);

        Root<T> root = applySpecificationToCriteria(spec, domainClass, query);
        query.select(root);

        TypedQuery<T> q = em.createQuery(query);

        q.setHint(QueryHints.PASS_DISTINCT_THROUGH, false);

        return q;
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
