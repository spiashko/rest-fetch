package com.spiashko.rfetch.parser;

import lombok.Getter;

import java.util.List;
import java.util.Stack;

@Getter
public class RfetchAsListVisitor implements RfetchVisitor<List<String>, List<String>> {

    public static final RfetchAsListVisitor INSTANCE = new RfetchAsListVisitor();

    @Override
    public List<String> visit(RfetchNode node, List<String> list) {
        if (node.isLeaf()) {
            Stack<String> textStack = new Stack<>();
            RfetchNode n = node;
            while (!n.isRoot()) {
                textStack.push(n.getName());
                n = n.getParent();
            }

            StringBuilder builder = new StringBuilder();
            builder.append(textStack.pop());
            while (!textStack.isEmpty()) {
                builder.append('.');
                builder.append(textStack.pop());
            }

            String path = builder.toString();

            list.add(path);

            return null;
        }

        for (RfetchNode child : node) {
            child.accept(this, list);
        }

        return list;
    }
}
