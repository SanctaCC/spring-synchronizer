package com.sanctacc.springsynchronizer;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@NoArgsConstructor
public class ServiceTest {

    private AtomicInteger counter = new AtomicInteger(0);

    @MethodSynchronized
    public boolean testMethodSynchronized() throws InterruptedException {
        int val = counter.addAndGet(1);
        Thread.yield();
        if (val > 1) {
            return false;
        }
        counter.set(0);
        return true;
    }
}
