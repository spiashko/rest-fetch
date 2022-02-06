package com.spiashko.jpafetch.allinone;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyPath;

import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.List;
import java.util.Objects;

public class FetchAllInOneSupport {

    public static Specification<Object> toSpecification(List<String> includedPaths) {
        return includedPaths.stream()
                .map(FetchAllInOneSupport::buildSpec)
                .reduce(Specification.where(null),
                        Specification::and);
    }

    private static Specification<Object> buildSpec(String attributePath) {
        return (root, query, builder) -> {
            PropertyPath path = PropertyPath.from(attributePath, root.getJavaType());
            FetchParent<Object, Object> f = traversePath(root, path);
            Join<Object, Object> join = (Join<Object, Object>) f;

            query.distinct(true);

            return join.getOn();
        };
    }

    private static FetchParent<Object, Object> traversePath(FetchParent<?, ?> root, PropertyPath path) {
        FetchParent<Object, Object> result = root.fetch(path.getSegment(), JoinType.LEFT);
        return path.hasNext() ? traversePath(result, Objects.requireNonNull(path.next())) : result;
    }

}
