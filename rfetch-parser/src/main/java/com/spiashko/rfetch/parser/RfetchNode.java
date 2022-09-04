package com.spiashko.rfetch.parser;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.*;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RfetchNode implements Iterable<RfetchNode> {

    private final RfetchNode parent;
    private final String name;
    private final Class<?> type;
    private final List<Annotation> annotations;
    private final List<RfetchNode> children;

    public static RfetchNode createRoot(Class<?> domainClass) {
        return RfetchNode.builder()
                .parent(null)
                .name("root")
                .type(domainClass)
                .annotations(new ArrayList<>())
                .children(new LinkedList<>())
                .build();
    }

    public <R, A> R accept(RfetchVisitor<R, A> visitor, A param) {
        return visitor.visit(this, param);
    }

    @Override
    public Iterator<RfetchNode> iterator() {
        return children.iterator();
    }

    public RfetchNode addChild(String propertyName) {
        Class<?> propertyType = PropertyPath.from(propertyName, this.getType()).getType();
        Annotation[] annotations = ReflectionUtils.findRequiredField(this.getType(), propertyName).getAnnotations();
        RfetchNode child = RfetchNode.builder()
                .parent(this)
                .name(propertyName)
                .type(propertyType)
                .annotations(Arrays.asList(annotations))
                .children(new LinkedList<>())
                .build();

        children.add(child);
        return child;
    }

    public void merge(RfetchNode anotherNode) {
        if (!this.getType().equals(anotherNode.getType())) {
            throw new RuntimeException("you can merge only with the same type");
        }

        for (RfetchNode anotherNodeChild : anotherNode) {
            RfetchNode child = this.getChildren().stream()
                    .filter(n -> n.getName().equals(anotherNodeChild.getName()))
                    .findAny()
                    .orElse(null);
            if (child == null) {
                this.addChild(anotherNodeChild);
                continue;
            }

            child.merge(anotherNodeChild);
        }
    }

    public void addChild(RfetchNode child) {
        RfetchNode clone = child.deepClone(this);
        children.add(clone);
    }

    public List<RfetchNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public List<Annotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return CollectionUtils.isEmpty(children);
    }

    @Override
    public String toString() {
        return "RfetchNode{" +
                (parent == null ? "" : "parentType=" + parent.type.getSimpleName() + ", ") +
                "name='" + name + '\'' +
                ", type=" + type.getSimpleName() +
                ", children=" + children +
                '}';
    }

    public RfetchNode deepClone(RfetchNode newParent) {
        RfetchNode cloned = cloneWithoutChildren(newParent);

        for (RfetchNode child : this) {
            cloned.addChild(child);
        }

        return cloned;
    }

    public RfetchNode cloneWithoutChildren(RfetchNode newParent) {
        return RfetchNode.builder()
                .parent(newParent)
                .name(this.getName())
                .type(this.getType())
                .annotations(this.getAnnotations())
                .children(new LinkedList<>())
                .build();
    }
}
