package com.sanctacc.springsynchronizer;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockInterceptor implements HandlerInterceptor {

    private Map<HandlerMethod, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (shouldSkip((HandlerMethod) handler)) {
            return true;
        }
        ReentrantLock newLock = new ReentrantLock(true);
        Lock oldLock = lockMap.putIfAbsent((HandlerMethod) handler, newLock);
        if (oldLock == null) {
            newLock.lock();
        } else {
            oldLock.lock();
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (shouldSkip((HandlerMethod) handler)){
            return;
        }
        ReentrantLock lock = lockMap.get(handler);
        if (!lock.hasQueuedThreads()) {
            lockMap.remove(handler);
        }
        lock.unlock();
    }

    private boolean shouldSkip(HandlerMethod method) {
        return !method.hasMethodAnnotation(MethodSynchronized.class);
    }

}