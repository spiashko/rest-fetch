package com.spiashko.jpafetch.jacksonjpa.selfrefresolution;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SelfReferenceResolutionUtils {

    public static String getPathToTest(final JsonGenerator jgen) {
        StringBuilder nestedPath = new StringBuilder();
        JsonStreamContext sc = jgen.getOutputContext();
        return traversePath(nestedPath, sc);
    }

    public static String getPathToTest(final PropertyWriter writer, final JsonGenerator jgen) {
        StringBuilder nestedPath = new StringBuilder();
        nestedPath.append(writer.getName());
        JsonStreamContext sc = jgen.getOutputContext();
        if (sc != null) {
            sc = sc.getParent();
        }
        return traversePath(nestedPath, sc);
    }

    private static String traversePath(StringBuilder nestedPath, JsonStreamContext sc) {
        while (sc != null) {
            if (sc.getCurrentName() != null) {
                if (nestedPath.length() > 0) {
                    nestedPath.insert(0, ".");
                }
                nestedPath.insert(0, sc.getCurrentName());
            }
            sc = sc.getParent();
        }
        return nestedPath.toString();
    }
}
