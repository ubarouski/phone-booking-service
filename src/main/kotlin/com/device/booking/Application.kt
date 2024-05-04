package com.device.booking

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@OpenAPIDefinition(
    info = Info(
        title = "Device booking",
        version = "1.0",
        description = "The service provides restAPI for device booking"
    )
)
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}