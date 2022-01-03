package com.spiashko.restpersistence.rfetch.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RfetchSpec {

    String requestParamName() default "include";

}
