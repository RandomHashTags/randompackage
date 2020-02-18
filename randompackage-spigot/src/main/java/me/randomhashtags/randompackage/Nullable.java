package me.randomhashtags.randompackage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/*
    Indicates that the target parameter can be null, and it won't cause issues being not null either
 */
@Target(ElementType.PARAMETER)
public @interface Nullable {
}
