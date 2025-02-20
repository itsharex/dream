package com.moxa.dream.template.annotation;

import com.moxa.dream.template.condition.Condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Conditional {
    String table() default "";

    boolean nullFlag() default true;

    boolean or() default false;

    Class<? extends Condition> value();


}
