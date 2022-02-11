package com.spiashko.jpafetch.parser;

import org.springframework.data.mapping.PropertyPath;

public class RfetchCompiler {

    public static RfetchNode compile(String include, Class<?> domainClass) {
        RfetchNode root = RfetchNode.createRoot(domainClass);
        parseString(include, root);
        return root;
    }

    private static int parseString(String input, RfetchNode parent) {

        StringBuilder currentString = new StringBuilder();

        int index = 0;
        if (input.charAt(0) == '(') {
            index++;
        }

        while (index < input.length()) {
            char c = input.charAt(index);

            if (c == ')') { // end of sublist, return
                buildLeaf(parent, currentString.toString());
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
        parent.addLeaf(leaf);
        return leaf;
    }

}
