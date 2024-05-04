package com.device.booking.dto

import jakarta.validation.constraints.NotEmpty


data class BookingRequestBody(
    val userName: String? = null,
    @field:NotEmpty
    val action: Action
)

enum class Action { BOOK, RETURN }
