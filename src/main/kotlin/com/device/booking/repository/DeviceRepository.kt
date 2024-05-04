package com.device.booking.repository

import com.device.booking.model.DeviceInfo
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository : CoroutineCrudRepository<DeviceInfo, Long>{
    fun findAllByBrandAndModel(brand: String, model: String): Flow<DeviceInfo?>
}