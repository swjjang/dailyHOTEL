package com.twoheart.dailyhotel.network;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by sheldon on 2016. 12. 12..
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface SelfTag
{
    String value();
}
