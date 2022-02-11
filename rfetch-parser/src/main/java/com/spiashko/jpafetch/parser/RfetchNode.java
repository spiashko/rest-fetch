package com.spiashko.jpafetch.parser;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RfetchNode {

    private final RfetchNode parent;

    private final String name;
    private final Class<?> type;

    private final List<RfetchNode> leafs = new ArrayList<>();

    private RfetchNode(RfetchNode parent, String name, Class<?> type) {
        this.parent = parent;
        this.name = name;
        this.type = type;
    }

    public void addLeaf(RfetchNode leaf) {
        leafs.add(leaf);
    }

    public List<RfetchNode> getLeafs() {
        return Collections.unmodifiableList(leafs);
    }

    public boolean isRoot() {
        return parent == null;
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
                ", leafs=" + leafs +
                '}';
    }
}
