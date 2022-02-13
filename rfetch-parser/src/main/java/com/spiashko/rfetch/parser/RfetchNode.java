package com.spiashko.rfetch.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RfetchNode implements Iterable<RfetchNode> {

    private final RfetchNode parent;

    private final String name;
    private final Class<?> type;

    private final List<RfetchNode> children = new ArrayList<>();

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

    static RfetchNode createLeaf(RfetchNode parent, String propertyName, Class<?> propertyType) {
        return new RfetchNode(parent, propertyName, propertyType);
    }

    static RfetchNode createRoot(Class<?> domainClass) {
        return new RfetchNode(null, "root", domainClass);
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


}
