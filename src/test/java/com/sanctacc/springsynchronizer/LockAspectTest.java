package com.sanctacc.springsynchronizer;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Stream;

@SpringBootTest(classes = TestApplication.class)
@RunWith(SpringRunner.class)
public class LockAspectTest {

    @Autowired
    private ServiceTest serviceTest;

    private final int THREAD_COUNT = 100;

    @Test
    public void simpleMethodSynchronizes() throws InterruptedException, BrokenBarrierException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(THREAD_COUNT + 1);
        final boolean[] testSuccessful = {true};
        Stream.iterate(0, i-> ++i).limit(THREAD_COUNT).map(thread -> new Thread(() -> {
            try {
                cyclicBarrier.await();
                if(!serviceTest.testMethodSynchronized()) {
                    testSuccessful[0] = false;
                }
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        })).forEach(Thread::start);

        cyclicBarrier.await();
        cyclicBarrier.reset();
        cyclicBarrier.await();
        Assert.assertThat(testSuccessful[0], CoreMatchers.is(true));
    }

}
