package com.device.booking.dto


import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PhoneBooking(
    val brand: String,
    val model: String,
    val imei: Long,
    val band: String? = null,
    val deviceOS: String? = null,
    val available: Boolean,
    val bookedBy: String? = null,
    val bookingDate: LocalDateTime? = null
)
