package com.sanctacc.springsynchronizer;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

@Aspect
public class LockAspect {

    private ConcurrentHashMap<LockedResource, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    private ThreadLocal<LockedResource> localResource = new ThreadLocal<>();
    private ThreadLocal<ReentrantLock> currentLock = new ThreadLocal<>();

    @Before("@annotation(methodSynchronized)")
    public void before(JoinPoint pjp, MethodSynchronized methodSynchronized) throws Throwable {
        LockedResource lockedResource = getLockedResource(pjp);
        localResource.set(lockedResource);
        ReentrantLock newLock = new ReentrantLock(methodSynchronized.fair());
        ReentrantLock oldLock = lockMap.putIfAbsent(lockedResource, newLock);
        if (oldLock == null) {
            currentLock.set(newLock);
            newLock.lock();
        } else {
            currentLock.set(oldLock);
            if (!oldLock.tryLock(methodSynchronized.timeout(), TimeUnit.MILLISECONDS)) {
                throw new TimeoutException(pjp.getSignature().getName() + " failed to acquire lock within "
                        + methodSynchronized.timeout() + "ms");
            }
        }
    }

    @AfterReturning("@annotation(methodSynchronized)")
    @AfterThrowing("@annotation(methodSynchronized)")
    public void after(JoinPoint pjp, MethodSynchronized methodSynchronized) {
        LockedResource lockedResource = localResource.get();
        ReentrantLock lock = lockMap.get(lockedResource);
        if (lock == null) {
            lock = currentLock.get();
            if (!lock.hasQueuedThreads()) {
                lockMap.keySet(lock).getMap().clear();
            }
        }
        if (!lock.hasQueuedThreads()) {
            lockMap.remove(lockedResource);
        }
        lock.unlock();
    }

    private LockedResource getLockedResource(JoinPoint jp) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        Annotation[][] annotations = method.getParameterAnnotations();
        Object[] arguments = jp.getArgs();
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < annotations.length; i++) {
            for (int i1 = 0; i1 < annotations[i].length; i1++) {
                if (annotations[i][i1].annotationType().equals(SynchronizeOn.class)) {
                    list.add(arguments[i]);
                    break;
                }
            }
        }
        return new LockedResource(method, list);
    }
}