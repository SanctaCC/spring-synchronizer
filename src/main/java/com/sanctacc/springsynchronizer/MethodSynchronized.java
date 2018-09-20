package com.sanctacc.springsynchronizer;

import java.lang.annotation.*;

@Target(value={ElementType.METHOD})
@Retention(value= RetentionPolicy.RUNTIME)
@Inherited
public @interface MethodSynchronized {
}