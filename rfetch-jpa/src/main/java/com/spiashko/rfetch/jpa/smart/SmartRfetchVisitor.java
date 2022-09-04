package com.spiashko.rfetch.jpa.smart;

import com.spiashko.rfetch.parser.RfetchNode;
import com.spiashko.rfetch.parser.RfetchVisitor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.QueryHints;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
class SmartRfetchVisitor implements RfetchVisitor<List<?>, Void> {

    private final EntityManager em;

    private final Map<NodeKey, Set<?>> fetchedObjects = new HashMap<>();
    private final Map<NodeKey, List<RfetchNode>> subFetches;

    public SmartRfetchVisitor(EntityManager em, RfetchNode root) {
        this.em = em;
        SubFetchesBuilder subFetchesBuilder = new SubFetchesBuilder();
        RfetchNode initialSubFetch = subFetchesBuilder.buildSubFetches(root);
        this.subFetches = subFetchesBuilder.getSubFetches();
        postConstruct(root, initialSubFetch);
    }

    private void postConstruct(RfetchNode root, RfetchNode initialSubFetch) {
        // fetch root with filter and page data
        NodeKey key = new NodeKey(root);
        List<Object> objects = getQuery(initialSubFetch).getResultList();
        fetchedObjects.put(key, new HashSet<>(objects));
        putNewlyFetchedObjects(objects, initialSubFetch);
    }

    @Override
    public List<?> visit(RfetchNode node, Void v) {
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

        return new ArrayList<>(objects);
    }

    private void processSubFetch(Set<?> objects, RfetchNode subFetch) {
        Collection<?> enrichedEntities = getQuery(objects, subFetch).getResultList();
        putNewlyFetchedObjects(enrichedEntities, subFetch);
    }

    private <T> TypedQuery<T> getQuery(RfetchNode subFetch) {
        return getQuery((root, cb) -> cb.equal(cb.literal(1), 1), subFetch);
    }

    private <T> TypedQuery<T> getQuery(@Nullable Set<T> entities, RfetchNode subFetch) {
        return getQuery((root, cb) -> root.in(entities), subFetch);
    }

    private <T> TypedQuery<T> getQuery(BiFunction<Root<T>, CriteriaBuilder, Predicate> spec, RfetchNode subFetch) {

        Class<T> rootClass = (Class<T>) subFetch.getType();

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(rootClass);
        Root<T> root = query.from(rootClass);

        recursiveFetch(root, subFetch);

        query.select(root);
        query.where(spec.apply(root, builder));

        TypedQuery<T> q = em.createQuery(query);

        q.setHint(QueryHints.PASS_DISTINCT_THROUGH, false);

        return q;
    }

    private void recursiveFetch(FetchParent root, RfetchNode node) {
        for (RfetchNode child : node) {
            FetchParent fetch = root.fetch(child.getName(), JoinType.LEFT);
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

            Set<Object> nestedObjects = new HashSet<>();
            for (Object enrichedEntity : entities) {
                Object e = Hibernate.unproxy(enrichedEntity); // it should be already initialized
                Field field = FieldUtils.getField(e.getClass(), childNodeName, true);
                Object nestedObject = ReflectionUtils.getField(field, e);
                if (nestedObject instanceof Collection) {
                    nestedObjects.addAll((Collection<Object>) nestedObject);
                } else {
                    nestedObjects.add(nestedObject);
                }
            }
            fetchedObjects.put(key, nestedObjects);
            putNewlyFetchedObjects(nestedObjects, childNode);
        }
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class NodeKey {
        private final Class<?> type;
        private final String name;

        public NodeKey(RfetchNode node) {
            this.name = node.getName();
            this.type = node.getType();
        }
    }

    @Getter
    private static class SubFetchesBuilder {

        private static final List<Class<? extends Annotation>> toManyAnnotations =
                Arrays.asList(ManyToMany.class, OneToMany.class);

        private static final List<Class<? extends Annotation>> toOneAnnotations =
                Arrays.asList(OneToOne.class, ManyToOne.class);

        private final Map<NodeKey, List<RfetchNode>> subFetches = new HashMap<>();

        private RfetchNode buildSubFetches(RfetchNode node) {

            List<RfetchNode> usedChildren = new ArrayList<>();

            RfetchNode cloneForPhaseOne = node.cloneWithoutChildren(null);
            node.getChildren().stream()
                    .filter(c -> c.getAnnotations().stream().anyMatch(a -> toOneAnnotations.contains(a.annotationType())))
                    .forEach(candidate -> {
                        RfetchNode subFetch = buildSubFetches(candidate);
                        cloneForPhaseOne.addChild(subFetch);
                        usedChildren.add(candidate);
                    });

            node.getChildren().stream()
                    .filter(c -> c.getAnnotations().stream().anyMatch(a -> toManyAnnotations.contains(a.annotationType())))
                    .findAny()
                    .ifPresent(candidate -> {
                        RfetchNode subFetch = buildSubFetches(candidate);
                        cloneForPhaseOne.addChild(subFetch);
                        usedChildren.add(candidate);
                    });

            List<RfetchNode> notUsedChild = node.getChildren().stream()
                    .filter(c -> !usedChildren.contains(c))
                    .collect(Collectors.toList());

            notUsedChild.forEach(c -> {
                RfetchNode cloneForPhaseTwo = node.cloneWithoutChildren(null);

                RfetchNode subFetch = buildSubFetches(c);
                cloneForPhaseTwo.addChild(subFetch);

                subFetches.compute(new NodeKey(cloneForPhaseTwo), (k, oldV) -> {
                    ArrayList<RfetchNode> result = new ArrayList<>();
                    result.add(cloneForPhaseTwo);
                    if (oldV != null) {
                        result.addAll(oldV);
                    }
                    return result;
                });
                usedChildren.add(c);
            });

            if (node.getChildren().size() != usedChildren.size()) {
                throw new IllegalStateException("not all children was processed");
            }

            return cloneForPhaseOne;
        }
    }

}
