package com.visable.messaging.controller.exceptionhandler

import com.visable.messaging.exception.MessageValidationException
import com.visable.messaging.exception.UserNicknameUniquenessException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun editionNotSupportedExceptionHandler(e: MethodArgumentNotValidException): ResponseEntity<Any> {
        val body = mutableMapOf<String, List<String?>>()
        val errors: List<String?> = e.bindingResult.fieldErrors.map { it.defaultMessage }.toList()

        body[ERROR_KEY] = errors
        return ResponseEntity(body, BAD_REQUEST)
    }

    @ExceptionHandler(UserNicknameUniquenessException::class)
    fun userNicknameUniquenessExceptionHandler(e: UserNicknameUniquenessException): ResponseEntity<Any> {
        val body = mapOf(ERROR_KEY to listOf(e.message))
        return ResponseEntity(body, CONFLICT)
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun missingRequestHeaderExceptionHandler(e: MissingRequestHeaderException): ResponseEntity<Any> {
        val body = mapOf(ERROR_KEY to listOf(e.message))
        return ResponseEntity(body, BAD_REQUEST)
    }

    @ExceptionHandler(MessageValidationException::class)
    fun messageValidationExceptionHandler(e: MessageValidationException): ResponseEntity<Any> {
        val body = mapOf(ERROR_KEY to listOf(e.message))
        return ResponseEntity(body, CONFLICT)
    }

    companion object {
        private const val ERROR_KEY = "errors"
    }
}
