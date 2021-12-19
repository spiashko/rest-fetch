package com.spiashko.restpersistence.rfetch;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RfetchPathsHolder {

    private List<String> includedPaths;
    private List<String> includedWithSubPaths;

    public void setIncludedPaths(List<String> includedPaths) {
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
