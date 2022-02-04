package com.spiashko.restpersistence.rfetch.core;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyPath;

import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RfetchSupport {

    public List<Specification<Object>> toSpecificationList(List<String> includedPaths) {
        List<Specification<Object>> rfetchSpecs = includedPaths.stream()
                .map(this::buildSpec)
                .collect(Collectors.toList());
        return rfetchSpecs;
    }

    public Specification<Object> toSpecification(List<String> includedPaths) {
        Specification<Object> reducedSpec = toSpecificationList(includedPaths).stream()
                .reduce(Specification.where(null),
                        Specification::and);
        return reducedSpec;
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
