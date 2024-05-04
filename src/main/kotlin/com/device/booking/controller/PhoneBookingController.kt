package com.device.booking.controller

import com.device.booking.dto.Action
import com.device.booking.dto.BookingRequestBody
import com.device.booking.service.PhoneService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Phone Booking APIs", description = "Phone Booking API for QA")
@RequestMapping("v1/phones")
class PhoneBookingController(private val phoneService: PhoneService) {

    @Operation(description = "Get phones availability ", summary = "Event")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Get phone booking status."),
        ApiResponse(responseCode = "404", description = "Phones not found."),
        ApiResponse(responseCode = "400", description = "Invalid request payload."),
        ApiResponse(responseCode = "500", description = "There is an internal error.")
    )
    @GetMapping
    suspend fun getPhones(@RequestParam brand: String, @RequestParam model: String) = withContext(Dispatchers.IO) {
        phoneService.findPhones(brand, model)
    }

    @Operation(description = "Book/return the phone ", summary = "Event")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Booked."),
        ApiResponse(responseCode = "404", description = "Phone not found."),
        ApiResponse(responseCode = "400", description = "Invalid request payload."),
        ApiResponse(responseCode = "500", description = "There is an internal error.")
    )
    @PostMapping("/{imei}")
    suspend fun book(@PathVariable imei: Long, @Valid @RequestBody requestBody: BookingRequestBody) =
        withContext(Dispatchers.IO) {
            when (requestBody.action) {
                Action.BOOK -> {
                    require(!requestBody.userName.isNullOrBlank()) { "Missing required non-blank userName" }
                    phoneService.bookPhone(imei, requestBody.userName)
                }

                Action.RETURN -> phoneService.returnPhone(imei)
            }
        }
}