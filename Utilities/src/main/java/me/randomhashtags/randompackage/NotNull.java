package me.randomhashtags.randompackage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/*
    Indicates that the target parameter should never be null, otherwise it might cause issues
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface NotNull {
}

