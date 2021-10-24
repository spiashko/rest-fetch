package com.spiashko.restpersistence.demo.cat.adapters.rfetch;

import com.spiashko.restpersistence.rfetch.RfetchValueCustomizer;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public class CatRfetchCustomizer implements RfetchValueCustomizer {
    @Override
    public String customize(String value) {
        if (value == null) {
            return null;
        }

        val newValue = value.replace("kids", "mKids;fKids");

        return newValue;
    }
}
