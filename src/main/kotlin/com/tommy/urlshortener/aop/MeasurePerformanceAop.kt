package com.tommy.urlshortener.aop

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class MeasurePerformanceAop {

    private val logger = KotlinLogging.logger { }

    @Around("@annotation(MeasurePerformance)")
    fun measurement(joinPoint: ProceedingJoinPoint): Any? {
        val startTime = System.currentTimeMillis()
        try {
            return joinPoint.proceed()
        } finally {
            val executionTime = calculateExecutionTime(startTime)
            logger.info { "<<< ${joinPoint.signature} executed in $executionTime" }
        }
    }

    private fun calculateExecutionTime(startTime: Long): String {
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        val seconds = duration / SECOND
        val milliseconds = duration % SECOND

        return if (seconds > ZERO) "$seconds s $milliseconds ms" else "$milliseconds ms"
    }

    companion object {
        private const val SECOND = 1000
        private const val ZERO = 0
    }
}
