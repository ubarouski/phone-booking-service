package com.device.booking.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("device_info")
data class DeviceInfo(
    @Id
    val id: Long? = null,
    val brand: String,
    val model: String,
    @Column("device_os")
    val deviceOS: String? = null,
    val band: String? = null,
)
