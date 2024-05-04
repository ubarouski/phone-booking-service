package com.device.booking.repository

import com.device.booking.model.Phone
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PhoneRepository : CoroutineCrudRepository<Phone, Long>{
    @Query("SELECT * FROM phone as p join device_info as di on p.device_id = di.id WHERE di.brand = :brand and di.model = :model")
    fun findPhones(brand: String, model: String): Flow<Phone>
}