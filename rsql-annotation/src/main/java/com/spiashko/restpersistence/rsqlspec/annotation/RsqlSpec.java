package com.spiashko.restpersistence.rsqlspec.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RsqlSpec {

    String requestParamName() default "filter";

    AndPathVarEq[] extensionFromPath() default {};

}
