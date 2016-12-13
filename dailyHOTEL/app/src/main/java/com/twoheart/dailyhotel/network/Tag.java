package com.twoheart.dailyhotel.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by sheldon on 2016. 12. 12..
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface TAG
{
    String value() default "";
}
