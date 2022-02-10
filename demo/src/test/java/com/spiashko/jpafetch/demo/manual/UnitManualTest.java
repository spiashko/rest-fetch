package com.spiashko.jpafetch.demo.manual;

import com.spiashko.jpafetch.demo.person.Person;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.data.mapping.PropertyPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnitManualTest {

    @Test
    void tryNewInclude() {
        //include=kittens(motherForKids,fatherForKids),bestFriend

//        Pattern p = Pattern.compile(".s");//. represents single character
//        Matcher m = p.matcher("as");
//        boolean b = m.matches();

//        PropertyPath path = PropertyPath.from(includedPath, domainClass);

        //include=(kittens(motherForKids,fatherForKids),bestFriend)
        IncludeNode root = IncludeCompiler.compile("(kittens(motherForKids,fatherForKids),bestFriend)", Person.class);
        System.out.println(root);

    }

    @Getter
    public static class IncludeNode {

        private final IncludeNode parent;

        private final String name;
        private final Class<?> type;

        private final List<IncludeNode> leafs = new ArrayList<>();

        private IncludeNode(IncludeNode parent, String name, Class<?> type) {
            this.parent = parent;
            this.name = name;
            this.type = type;
        }

        public void addLeaf(IncludeNode leaf) {
            leafs.add(leaf);
        }

        public List<IncludeNode> getLeafs() {
            return Collections.unmodifiableList(leafs);
        }

        public boolean isRoot() {
            return parent == null;
        }

        public static IncludeNode createLeaf(IncludeNode parent, String propertyName, Class<?> propertyType) {
            return new IncludeNode(parent, propertyName, propertyType);
        }

        public static IncludeNode createRoot(Class<?> domainClass) {
            return new IncludeNode(null, "root", domainClass);
        }

        @Override
        public String toString() {
            return "IncludeNode{" +
                    (parent == null ? "" : "parentType=" + parent.type.getSimpleName() + ", ") +
                    "name='" + name + '\'' +
                    ", type=" + type.getSimpleName() +
                    ", leafs=" + leafs +
                    '}';
        }
    }

    public static class IncludeCompiler {

        public static IncludeNode compile(String include, Class<?> domainClass) {
            IncludeNode root = IncludeNode.createRoot(domainClass);
            parseString(include, root);
            return root;
        }


        private static int parseString(String input, IncludeNode parent) {

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
                    IncludeNode leaf = buildLeaf(parent, currentString.toString());
                    currentString.delete(0, currentString.length());

                    int temp = parseString(input.substring(index + 1), leaf);
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

        private static IncludeNode buildLeaf(IncludeNode parent, String propertyName) {
            Class<?> propertyType = PropertyPath.from(propertyName, parent.getType()).getType();
            IncludeNode leaf = IncludeNode.createLeaf(parent, propertyName, propertyType);
            parent.addLeaf(leaf);
            return leaf;
        }

    }

}
