package com.spiashko.restpersistence.jacksonjpa.entitybyid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityByIdDeserialize {

    String value() default "id";

    Class<?> idClass() default String.class;

}
