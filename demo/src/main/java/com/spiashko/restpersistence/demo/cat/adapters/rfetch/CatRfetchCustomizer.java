package com.spiashko.restpersistence.demo.cat.adapters.rfetch;

import com.spiashko.restpersistence.demo.cat.Cat_;
import com.spiashko.restpersistence.rfetch.annotation.RfetchValueCustomizer;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CatRfetchCustomizer implements RfetchValueCustomizer {

    private static final String KIDS = "^([^.]*)(\\.?)kids(\\.?)([^.]*)$";
    private static final Pattern KIDS_PATTERN = Pattern.compile(KIDS);

    @Override
    public List<String> customize(final List<String> includedPaths) {
        if (CollectionUtils.isEmpty(includedPaths)) {
            return new ArrayList<>();
        }

        val newIncludedPaths = new ArrayList<String>();

        for (String includedPath : includedPaths) {
            Matcher matcher = KIDS_PATTERN.matcher(includedPath);
            if (matcher.matches()) {
                newIncludedPaths.add(getReplacement(matcher, Cat_.FATHER_FOR_KIDS));
                newIncludedPaths.add(getReplacement(matcher, Cat_.MOTHER_FOR_KIDS));
            } else {
                newIncludedPaths.add(includedPath);
            }

        }
        return newIncludedPaths;
    }

    private String getReplacement(Matcher matcher, String s) {
        return matcher.group(1) + matcher.group(2) + s + matcher.group(3) + matcher.group(4);
    }
}
