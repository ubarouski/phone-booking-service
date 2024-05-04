package com.device.booking.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("phone")
data class Phone(
    @Id
    val imei: Long,
    @Column("device_id")
    val deviceId: String,
    @Column("user_name")
    var userName: String? = null,
    @Column("booking_date")
    var bookingTime: LocalDateTime? = null
)
