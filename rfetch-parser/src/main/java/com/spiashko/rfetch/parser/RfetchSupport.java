package com.spiashko.rfetch.parser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mapping.PropertyPath;

import java.util.ArrayList;
import java.util.List;

public class RfetchSupport {

    public static List<String> effectedPaths(String include, Class<?> domainClass) {
        RfetchNode root = compile(include, domainClass);
        return effectedPaths(root);
    }

    public static List<String> effectedPaths(RfetchNode rfetchRoot) {
        if (rfetchRoot == null) {
            return null;
        }
        List<String> result = new ArrayList<>();
        rfetchRoot.accept(RfetchAsListVisitor.INSTANCE, result);
        return result;
    }

    public static RfetchNode compile(String include, Class<?> domainClass) {
        if (StringUtils.isBlank(include)) {
            return null;
        }
        RfetchNode root = RfetchNode.createRoot(domainClass);
        parseString(include, root);
        return root;
    }

    private static int parseString(String input, RfetchNode parent) {

        if (input.charAt(0) != '(') {
            throw new IllegalArgumentException("list of filed should start with round brackets");
        }

        StringBuilder currentString = new StringBuilder();
        int index = 1; // 0 index is '('

        while (index < input.length()) {
            char c = input.charAt(index);

            if (c == ')') { // end of sublist, return
                if (StringUtils.isNotEmpty(currentString.toString())) { //if empty then end of section - nothing to build
                    buildLeaf(parent, currentString.toString());
                }
                return index + 1;
            }

            if (c == '(') { // start of sublist, recursive call
                RfetchNode leaf = buildLeaf(parent, currentString.toString());
                currentString.delete(0, currentString.length());

                int temp = parseString(input.substring(index), leaf);
                index += temp;

                index++;
                continue;
            }

            if (c == ',') { // end of property entry, add to parent
                if (input.charAt(index - 1) == ')') {
                    index++;
                    continue;
                }

                buildLeaf(parent, currentString.toString());
                currentString.delete(0, currentString.length());

                index++;
                continue;
            }

            currentString.append(c);
            index++;
        }
        return 0;
    }

    private static RfetchNode buildLeaf(RfetchNode parent, String propertyName) {
        Class<?> propertyType = PropertyPath.from(propertyName, parent.getType()).getType();
        RfetchNode leaf = RfetchNode.createLeaf(parent, propertyName, propertyType);
        parent.addChild(leaf);
        return leaf;
    }

}
