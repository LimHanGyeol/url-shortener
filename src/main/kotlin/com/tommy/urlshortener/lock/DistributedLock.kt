package com.tommy.urlshortener.lock

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(

    val key: String,

    val timeUnit: TimeUnit = TimeUnit.SECONDS,

    /**
     * 락 대기 시간. 락 획득을 위해 waitTime 만큼 대기한다.
     */
    val waitTime: Long = 5L,

    /**
     * 락 임대 시간. 락을 획득한 후 leaseTime이 지나면 락을 해제한다.
     */
    val leaseTime: Long = 3L,
)
