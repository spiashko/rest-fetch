package com.spiashko.jpafetch.fetch.smart;

import com.spiashko.jpafetch.parser.RfetchNode;
import com.spiashko.jpafetch.parser.RfetchVisitor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;


@SuppressWarnings({"rawtypes", "unchecked"})
@RequiredArgsConstructor
class SmartRfetchVisitor implements RfetchVisitor<Void, Collection<?>> {

    private final EntityManager em;

    @Override
    public Void visit(RfetchNode node, Collection<?> entities) {

        for (RfetchNode child : node) {
            processChild(node, entities, child);
        }

        return null;
    }

    private void processChild(RfetchNode node, Collection<?> entities, RfetchNode child) {
        String childNodeName = child.getName();

        Specification limitedByCollectionSpec = (root, query, builder) -> root.in(entities);
        Specification fetchSpec = (root, query, builder) -> {
            root.fetch(childNodeName, JoinType.LEFT);
            return null;
        };
        Specification finalSpec = fetchSpec.and(limitedByCollectionSpec);

        //actual work is done here
        Collection<Object> enrichedEntities = getQuery(finalSpec, node.getType()).getResultList();

        if (child.isLeaf()) {
            return;
        }

        Collection<Object> nestedObjects = new ArrayList<>();
        for (Object e : enrichedEntities) {
            Field field = FieldUtils.getField(e.getClass(), childNodeName, true);
            Object nestedObject = ReflectionUtils.getField(field, e);
            if (nestedObject instanceof Collection) {
                nestedObjects.addAll((Collection<Object>) nestedObject);
            } else {
                nestedObjects.add(nestedObject);
            }
        }

        child.accept(this, nestedObjects);
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
