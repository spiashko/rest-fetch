package com.spiashko.restpersistence.spec;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyPath;

import javax.persistence.criteria.*;
import java.util.Objects;
import java.util.function.BiFunction;

public class SpecSupport {

    public static Specification<Object> buildConditionSpec(
            String attributePath,
            BiFunction<Expression<?>, CriteriaBuilder, Predicate> conditionApplier) {
        return (root, query, builder) -> {
            PropertyPath path = PropertyPath.from(attributePath, root.getJavaType());
            Expression<Object> expression = traversePathWithGet(root, path);
            return conditionApplier.apply(expression, builder);
        };
    }

    public static Specification<Object> buildFetchSpec(String attributePath) {
        return (root, query, builder) -> {
            PropertyPath path = PropertyPath.from(attributePath, root.getJavaType());
            FetchParent<Object, Object> f = traversePathWithFetch(root, path);
            Join<Object, Object> join = (Join<Object, Object>) f;

            query.distinct(true);

            return join.getOn();
        };
    }

    public static Expression<Object> traversePathWithGet(Path<?> root, PropertyPath path) {
        Path<Object> result = root.get(path.getSegment());
        return path.hasNext() ? traversePathWithGet(result, Objects.requireNonNull(path.next())) : result;
    }

    public static FetchParent<Object, Object> traversePathWithFetch(FetchParent<?, ?> root, PropertyPath path) {
        FetchParent<Object, Object> result = root.fetch(path.getSegment(), JoinType.LEFT);
        return path.hasNext() ? traversePathWithFetch(result, Objects.requireNonNull(path.next())) : result;
    }

}
