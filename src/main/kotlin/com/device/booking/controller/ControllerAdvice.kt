package com.device.booking.controller

import com.device.booking.exceptions.AlreadyBookedException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ControllerAdvice : ResponseEntityExceptionHandler() {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFoundException(ex: Exception) =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    ex.message ?: HttpStatus.NOT_FOUND.reasonPhrase
                )
            )

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequestException(ex: Exception) =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    ex.message ?: HttpStatus.BAD_REQUEST.reasonPhrase
                )
            )


    @ExceptionHandler(AlreadyBookedException::class)
    fun handleConflictException(ex: Exception) =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    ex.message ?: HttpStatus.CONFLICT.reasonPhrase
                )
            )

}
