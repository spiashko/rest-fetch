package com.spiashko.restpersistence.rsqlspec.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface AndPathVarEq {

    String pathVar();

    String attributePath();

}
