package com.tommy.urlshortener.exception

import com.tommy.urlshortener.dto.ErrorResult
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import java.text.MessageFormat
import jakarta.validation.ConstraintViolationException
import jakarta.validation.UnexpectedTypeException

@RestControllerAdvice
class DefaultControllerAdvice {

    private val logger = KotlinLogging.logger { }

    /**
     * 비즈니스 익셉션
     */
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ErrorResult> {
        val formattedMessage = MessageFormat.format(e.default, *e.args)

        logger.error { formattedMessage }

        val errorResult = ErrorResult(
            code = e.code,
            message = formattedMessage,
        )

        return ResponseEntity(errorResult, HttpStatus.valueOf(e.status))
    }

    /**
     * org.springframework.validation 의 validation 실패(@Valid)로 인해 발생하는 예외를 처리.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatchException(e: MethodArgumentNotValidException): ErrorResult {
        logger.error(e) { "MethodArgumentNotValidException" }

        val errorFields = e.bindingResult.fieldErrors.map {
            ErrorResult.ErrorField(field = it.field, value = it.rejectedValue, reason = it.defaultMessage)
        }

        return ErrorResult(
            code = "methodArgumentNotValid",
            message = "다음 항목들은 유효한 값이 아닙니다.",
            errorFields = errorFields,
        )
    }

    /**
     * Exception Handler
     * 기본 예외 핸들러, Http Response 500
     */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Exception): ErrorResult {
        logger.error(e) { "throw Exception" }

        return ErrorResult(
            code = "internalError",
            message = "Internal server error occurred. Please contact with administrator. (${e.message})",
        )
    }

    /**
     * @description 요청 URL에 해당하는 핸들러(Controller)가 없을 경우 발생하는 예외를 처리.
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoHandlerFoundException(e: NoHandlerFoundException): ErrorResult {
        logger.error(e) { "NoHandlerFoundException" }

        return ErrorResult(
            code = "handlerNotFound",
            message = "Request URL(${e.requestURL}) is not found.",
        )
    }

    /**
     * HttpMessageNotReadableException: HTTP 요청 본문을 Java 객체로 변환할 수 없을 때 발생하는 예외를 처리.(JSON 형식, Type Mismatch)
     * UnsatisfiedServletRequestParameterException: 요청 매개변수가 특정 조건을 만족하지 않을 때. params에 정의한 조건을 만족하지 않는 경우 발생하는 예외를 처리.
     * MissingServletRequestParameterException: 필수 요청 매개변수가 누락됐을 때 발생하는 예외를 처리.
     * UnexpectedTypeException: Bean Validation 시 예상하지 못한 타입의 값이 들어올 때 발생하는 예외를 처리. @Size(min = 5) 일 경우 해당 타입이 boolean으로 전달 될 경우.
     */
    @ExceptionHandler(
        value = [
            HttpMessageNotReadableException::class,
            UnsatisfiedServletRequestParameterException::class,
            MissingServletRequestParameterException::class,
            UnexpectedTypeException::class,
        ]
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(e: Exception): ErrorResult {
        val exceptionName = e::class.simpleName
        logger.error(e) { exceptionName }

        return ErrorResult(
            code = "badRequest.$exceptionName",
            message = "Bad request: ${e.localizedMessage}",
        )
    }

    /**
     * MethodArgumentTypeMismatchException: Controller의 매개변수 타입과 요청에서 전달된 값의 타입이 일치하지 않을 때 발생하는 예외를 처리.
     * 매개변수 타입이 Integer 일 경우 요청으로 다른 형식의 값이 전달 될 경우.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ErrorResult {
        logger.error(e) { "MethodArgumentTypeMismatchException" }

        return ErrorResult(
            code = "invalidType.${e.requiredType?.simpleName ?: "null"}",
            message = "`${e.name}`'s type should be `${e.requiredType?.simpleName}`.",
        )
    }

    /**
     * ConstraintViolationException: Bean Validation에서 @Validated를 사용하여 @PathVariable, @RequestParam, @RequestHeader 등 값에 대해 유효성 검사가 실패 할 때 발생하는 예외를 처리.
     */
    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationException(e: ConstraintViolationException): ErrorResult {
        logger.error(e) { "ConstraintViolationException" }

        val errorFields = e.constraintViolations.map { violation ->
            val constraint = violation.constraintDescriptor.constraintValidatorClasses.firstOrNull()
            val node = violation.propertyPath.lastOrNull()

            val reason = MessageFormat.format("Constraint failed: {0}", node?.name)

            ErrorResult.ErrorField(
                field = "constraintViolation.${constraint?.simpleName ?: "null"}", value = violation.invalidValue, reason = reason,
            )
        }

        return ErrorResult(code = "constraintViolation", message = "다음 항목들은 유효한 값이 아닙니다.", errorFields = errorFields)
    }

    /**
     * BindException Handler: 일반적인 데이터 바인딩 실패 시 발생하는 예외를 처리.
     */
    @ExceptionHandler(BindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBindException(e: BindException): ErrorResult {
        logger.error(e) { "BindException" }

        val errorFields = e.bindingResult.fieldErrors.map {
            ErrorResult.ErrorField(
                field = it.field, value = it.rejectedValue, reason = it.defaultMessage
            )
        }

        return ErrorResult(
            code = "bindException",
            message = "다음 항목들은 유효한 값이 아닙니다.",
            errorFields = errorFields,
        )
    }
}
