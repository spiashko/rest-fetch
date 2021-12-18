package com.spiashko.restpersistence.demo.cat.adapters.rfetch;

import com.spiashko.restpersistence.demo.cat.Cat_;
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

        val newValue = value.replaceAll("([^;.]*)(\\.?)kids", "$1$2" + Cat_.FATHER_FOR_KIDS + ";" + "$1$2"  + Cat_.MOTHER_FOR_KIDS);

        return newValue;
    }
}
