package com.spiashko.rfetch.jpa.smart;

import com.spiashko.rfetch.jpa.ObjectUtils;
import com.spiashko.rfetch.parser.RfetchNode;
import com.spiashko.rfetch.parser.RfetchVisitor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.QueryHints;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BiFunction;

@Slf4j
class SmartRfetchVisitor implements RfetchVisitor<Void, Void> {

    private final EntityManager em;
    private final Map<NodeKey, List<RfetchNode>> subFetches;
    private final Map<NodeKey, Set<?>> fetchedObjects = new HashMap<>();

    public SmartRfetchVisitor(EntityManager em,
                              Map<NodeKey, List<RfetchNode>> subFetches,
                              RfetchNode root, RfetchNode initialSubFetch,
                              List<?> initialObjects) {
        this.em = em;
        this.subFetches = subFetches;
        NodeKey key = new NodeKey(root);
        fetchedObjects.put(key, new HashSet<>(initialObjects));
        putNewlyFetchedObjects(initialObjects, initialSubFetch);
    }

    @Override
    public Void visit(RfetchNode node, Void v) {
        NodeKey key = new NodeKey(node);

        Set<?> objects = fetchedObjects.get(key);
        if (objects == null) {
            throw new IllegalStateException("for some unknown reason objects wasn't fetched");
        }

        List<RfetchNode> nodeFetches = subFetches.get(key);
        if (nodeFetches != null) {
            for (RfetchNode subFetch : nodeFetches) {
                processSubFetch(objects, subFetch);
            }
        }

        for (RfetchNode child : node) {
            child.accept(this, v);
        }

        return null;
    }

    private void processSubFetch(Set<?> objects, RfetchNode subFetch) {
        Collection<?> enrichedEntities = getQuery(objects, subFetch).getResultList();
        putNewlyFetchedObjects(enrichedEntities, subFetch);
    }

    private <S> TypedQuery<S> getQuery(@Nullable Set<S> entities, RfetchNode subFetch) {
        return getQuery((root, cb) -> root.in(entities), subFetch);
    }

    @SuppressWarnings("unchecked")
    private <S> TypedQuery<S> getQuery(BiFunction<Root<S>, CriteriaBuilder, Predicate> spec, RfetchNode subFetch) {

        Class<S> rootClass = (Class<S>) subFetch.getType();

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<S> query = builder.createQuery(rootClass);
        Root<S> root = query.from(rootClass);

        recursiveFetch(root, subFetch);

        query.select(root);
        query.where(spec.apply(root, builder));

        query.distinct(true);

        TypedQuery<S> q = em.createQuery(query);
        q.setHint(QueryHints.PASS_DISTINCT_THROUGH, false);
        return q;
    }

    private void recursiveFetch(FetchParent<?, ?> root, RfetchNode node) {
        for (RfetchNode child : node) {
            FetchParent<?, ?> fetch = root.fetch(child.getName(), JoinType.LEFT);
            recursiveFetch(fetch, child);
        }
    }

    private void putNewlyFetchedObjects(Collection<?> entities, RfetchNode subFetch) {
        for (RfetchNode childNode : subFetch) {
            NodeKey key = new NodeKey(childNode);
            String childNodeName = childNode.getName();
            if (fetchedObjects.containsKey(key)) {
                log.warn("entities was already fetched");
            }
            Set<Object> nestedObjects = ObjectUtils.extractNestedObjects(entities, childNodeName);
            fetchedObjects.put(key, nestedObjects);
            putNewlyFetchedObjects(nestedObjects, childNode);
        }
    }

}
