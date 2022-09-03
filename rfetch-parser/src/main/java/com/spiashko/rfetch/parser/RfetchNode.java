package com.spiashko.rfetch.parser;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.*;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RfetchNode implements Iterable<RfetchNode>, Cloneable {

    private final RfetchNode parent;
    private final String name;
    private final Class<?> type;
    private final Annotation[] annotations;
    private final List<RfetchNode> children;

    static RfetchNode createLeaf(RfetchNode parent, String propertyName, Class<?> propertyType) {
        Annotation[] annotations = ReflectionUtils.findRequiredField(parent.getType(), propertyName).getAnnotations();
        return RfetchNode.builder()
                .parent(parent)
                .name(propertyName)
                .type(propertyType)
                .annotations(annotations)
                .children(new ArrayList<>())
                .build();
    }

    static RfetchNode createRoot(Class<?> domainClass) {
        return RfetchNode.builder()
                .parent(null)
                .name("root")
                .type(domainClass)
                .annotations(new Annotation[0])
                .children(new ArrayList<>())
                .build();
    }

    public <R, A> R accept(RfetchVisitor<R, A> visitor, A param) {
        return visitor.visit(this, param);
    }

    @Override
    public Iterator<RfetchNode> iterator() {
        return children.iterator();
    }

    public void addChild(RfetchNode leaf) {
        children.add(leaf);
    }

    public List<RfetchNode> getChildren() {
        return Collections.unmodifiableList(children);
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

    @Override
    public RfetchNode clone() {

        List<RfetchNode> children = new ArrayList<>();
        for (RfetchNode child : this) {
            children.add(child.clone());
        }

        return RfetchNode.builder()
                .parent(this.getParent())
                .name(this.getName())
                .type(this.getType())
                .annotations(this.getAnnotations())
                .children(children)
                .build();
    }
}
