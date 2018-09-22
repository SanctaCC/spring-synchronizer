package com.sanctacc.springsynchronizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  AutoConfiguration for {@link LockAspect}
 */
@Configuration
public class SynchronizerAutoConfiguration {

    @Bean
    public LockAspect lockAspect() {
        return new LockAspect();
    }

}