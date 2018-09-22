package com.sanctacc.springsynchronizer;

import java.lang.annotation.*;

/**
 * Indicates method parameter that should be included in {@link LockedResource} instance.
 * Note that lock uses actual equality and not just reference-equality i.e. non-null objects
 * o1 and o2 are equal if and only if o1.equals(o2). This also means that changing parameter's
 * state inside method may break synchronization and hinder performance.
 */
@Target(value={ElementType.PARAMETER})
@Retention(value= RetentionPolicy.RUNTIME)
@Inherited
public @interface SynchronizeOn {
}