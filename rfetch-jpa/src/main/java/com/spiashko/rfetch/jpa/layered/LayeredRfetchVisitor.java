package com.spiashko.rfetch.jpa.layered;

import com.spiashko.rfetch.parser.RfetchNode;
import com.spiashko.rfetch.parser.RfetchVisitor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.QueryHints;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;


@SuppressWarnings({"rawtypes", "unchecked"})
@RequiredArgsConstructor
class LayeredRfetchVisitor implements RfetchVisitor<Void, Collection<?>> {

    private final EntityManager em;

    @Override
    public Void visit(RfetchNode node, Collection<?> entities) {

        for (RfetchNode child : node) {
            processChild(node, entities, child);
        }

        return null;
    }

    //TODO: in theory we have two ways to process:
    // - which is already implemented which fully compatible with further mutations, so fetch to update
    // - second which will be fully detached where we will replace children with usual collection/object
    // it will allow fetch children directly without its parent (current implementation fetches parents twice)
    private void processChild(RfetchNode node, Collection<?> entities, RfetchNode child) {
        String childNodeName = child.getName();

        //actual work is done here
        Collection<Object> enrichedEntities = getQuery((Collection) entities, node.getType(), childNodeName).getResultList();

        if (child.isLeaf()) {
            return;
        }

        Collection<Object> nestedObjects = new HashSet<>();
        for (Object enrichedEntity : enrichedEntities) {
            Object e = Hibernate.unproxy(enrichedEntity); // it should be already initialized
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

    private <T> TypedQuery<T> getQuery(@Nullable Collection<T> entities, Class<T> rootClass, String childNodeName) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(rootClass);
        Root<T> root = query.from(rootClass);

        root.fetch(childNodeName, JoinType.LEFT);

        query.select(root);
        query.where(root.in(entities));

        TypedQuery<T> q = em.createQuery(query);

        q.setHint(QueryHints.PASS_DISTINCT_THROUGH, false);

        return q;
    }
}
