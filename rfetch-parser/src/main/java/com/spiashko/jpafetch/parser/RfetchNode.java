package com.spiashko.jpafetch.parser;

import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Getter
public class RfetchNode implements Iterable<RfetchNode> {

    private final RfetchNode parent;

    private final String name;
    private final Class<?> type;

    private final List<RfetchNode> children = new ArrayList<>();

    private RfetchNode(RfetchNode parent, String name, Class<?> type) {
        this.parent = parent;
        this.name = name;
        this.type = type;
    }

    public <R, A> R accept(RfetchVisitor<R, A> visitor, A param) {
        return visitor.visit(this, param);
    }

    public <R, A> R accept(RfetchVisitor<R, A> visitor) {
        return visitor.visit(this, null);
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
                ", leafs=" + children +
                '}';
    }


}
