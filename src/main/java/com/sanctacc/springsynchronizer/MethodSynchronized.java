package com.sanctacc.springsynchronizer;

import java.lang.annotation.*;

/**
 * Indicates method which execution should be synchronized.
 */
@Target(value={ElementType.METHOD})
@Retention(value= RetentionPolicy.RUNTIME)
@Inherited
public @interface MethodSynchronized {

    /**
     * Decides whether this synchronization should adhere to fair queuing rules.
     * Note that this may slow down the application dramatically but will also prevent thread starvation.
     */
    boolean fair() default false;

    /**
     * Timeout in milliseconds. If exceeded throws {@link java.util.concurrent.TimeoutException}
     */
    long timeout() default Long.MAX_VALUE;
}