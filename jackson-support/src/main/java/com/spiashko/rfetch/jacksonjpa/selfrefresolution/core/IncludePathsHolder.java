package com.spiashko.rfetch.jacksonjpa.selfrefresolution.core;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class IncludePathsHolder {

    private static ThreadLocal<ValueContainer> threadLocal = new ThreadLocal<>();

    public static List<String> getIncludedPaths() {
        return Optional.ofNullable(threadLocal.get())
                .map(ValueContainer::getIncludedPaths)
                .orElse(null);
    }

    public static void setIncludedPaths(List<String> includedPaths) {
        val container = new ValueContainer(includedPaths);
        threadLocal.set(container);
    }

    public static List<String> getIncludedWithSubPaths() {
        return Optional.ofNullable(threadLocal.get())
                .map(ValueContainer::getIncludedWithSubPaths)
                .orElse(null);
    }

    public static void remove() {
        threadLocal.remove();
    }


    @Getter
    private static class ValueContainer {

        private final List<String> includedPaths;
        private final List<String> includedWithSubPaths;

        public ValueContainer(List<String> includedPaths) {
            if (includedPaths == null) {
                includedPaths = Collections.emptyList();
            }
            this.includedPaths = includedPaths;
            this.includedWithSubPaths = includedPaths.stream()
                    .map(this::convertToAllSubPaths)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }

        private List<String> convertToAllSubPaths(String attributePath) {
            List<String> pathParts = Arrays.asList(attributePath.split("\\."));

            List<String> resultedSpecs = new ArrayList<>();

            for (int i = 0; i < pathParts.size(); i++) {
                String path = pathParts.stream()
                        .limit(i + 1L)
                        .collect(Collectors.joining("."));
                resultedSpecs.add(path);
            }

            return resultedSpecs;
        }
    }


}
