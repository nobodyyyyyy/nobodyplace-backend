package com.nobody.nobodyplace.utils.redis;

public interface ILock {

    boolean tryLock(long timeoutSec);

    void unlock();
}
