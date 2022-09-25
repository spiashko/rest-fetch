package com.spiashko.rfetch.jpa.smart;

import com.spiashko.rfetch.parser.RfetchNode;
import lombok.Getter;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

@Getter
class SubFetchesBuilder {

    private static final List<Class<? extends Annotation>> toManyAnnotations =
            Arrays.asList(ManyToMany.class, OneToMany.class);

    private static final List<Class<? extends Annotation>> toOneAnnotations =
            Arrays.asList(OneToOne.class, ManyToOne.class);

    private final Map<NodeKey, List<RfetchNode>> subFetches = new HashMap<>();

    RfetchNode buildSubFetches(RfetchNode node) {

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
