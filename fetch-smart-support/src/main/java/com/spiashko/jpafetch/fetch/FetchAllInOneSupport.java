package com.spiashko.jpafetch.fetch;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyPath;

import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.List;
import java.util.Objects;

public class FetchAllInOneSupport {

    public Specification<Object> toSpecification(List<String> includedPaths) {
        return includedPaths.stream()
                .map(this::buildSpec)
                .reduce(Specification.where(null),
                        Specification::and);
    }

    private Specification<Object> buildSpec(String attributePath) {
        return (root, query, builder) -> {
            PropertyPath path = PropertyPath.from(attributePath, root.getJavaType());
            FetchParent<Object, Object> f = traversePath(root, path);
            Join<Object, Object> join = (Join<Object, Object>) f;

            query.distinct(true);

            return join.getOn();
        };
    }

    private FetchParent<Object, Object> traversePath(FetchParent<?, ?> root, PropertyPath path) {
        FetchParent<Object, Object> result = root.fetch(path.getSegment(), JoinType.LEFT);
        return path.hasNext() ? traversePath(result, Objects.requireNonNull(path.next())) : result;
    }

}
