package com.tommy.urlshortener.lock

import com.tommy.urlshortener.exception.ServiceUnavailableException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Aspect
@Component
class DistributedLockAop(
    private val redissonClient: RedissonClient,
    private val transactionAop: TransactionAop,
) {
    private val logger = KotlinLogging.logger { }

    @Around("@annotation(com.tommy.urlshortener.lock.DistributedLock)")
    fun lock(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val distributedLock = method.getAnnotation(DistributedLock::class.java)

        val key = "$REDISSON_LOCK_PREFIX${SpELParser.getDynamicValue(signature.parameterNames, joinPoint.args, distributedLock.key)}"

        val lock = redissonClient.getLock(key)

        return try {
            val acquired = lock.tryLock(distributedLock.waitTime, distributedLock.leaseTime, distributedLock.timeUnit)

            if (!acquired) {
                logger.error { "lock acquired failed. key: $key" }
                return ServiceUnavailableException(LOCK_ACQUIRED_FAILED, key)
            }

            transactionAop.proceed(joinPoint)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            logger.error { "Current Thread Interrupt. Lock key: $key, message: ${e.message}" }
            throw e
        } finally {
            releaseLock(lock, key, method)
        }
    }

    private fun releaseLock(lock: RLock, key: String, method: Method) {
        try {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        } catch (e: IllegalMonitorStateException) {
            logger.error { "already unlock. key: ${keyValue("key", key)}, serviceName: ${keyValue("serviceName", method.name)}, message: ${e.message}" }
        } catch (e: Exception) {
            logger.error { "failed unlock. key: ${keyValue("key", key)}, serviceName: ${keyValue("serviceName", method.name)}, message: ${e.message}" }
        }
    }

    private fun keyValue(key: String, value: Any) = "$key: $value"

    companion object {
        private const val REDISSON_LOCK_PREFIX = "lock:"

        private val LOCK_ACQUIRED_FAILED = "lock.acquired.failed" to "lock acquired failed. key: {0}"
    }
}
