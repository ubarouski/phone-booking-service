package com.device.booking.service

import com.device.booking.client.DeviceClient
import com.device.booking.dto.PhoneBooking
import com.device.booking.exceptions.AlreadyBookedException
import com.device.booking.model.DeviceInfo
import com.device.booking.repository.DeviceRepository
import com.device.booking.repository.PhoneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import java.time.LocalDateTime

@Service
class PhoneService(
    private val phoneRepository: PhoneRepository,
    private val deviceRepository: DeviceRepository,
    private val deviceClient: DeviceClient
) {

    suspend fun findPhones(brand: String, model: String): Flow<PhoneBooking> {
        val phones = phoneRepository.findPhones(brand, model)
            .onEmpty { throw NoSuchElementException("No phones $brand $model found!") }
        val deviceInfo = try {
            deviceClient.getDevices(brand, model).firstOrNull()?.let {
                DeviceInfo(
                    brand = it.capabilities.brand,
                    model = it.capabilities.model,
                    deviceOS = it.capabilities.deviceOS
                )
            }
        } catch (ex: RestClientException) {
            deviceRepository.findAllByBrandAndModel(brand, model).firstOrNull()
        }
        return phones.map {
            PhoneBooking(
                brand = deviceInfo?.brand ?: brand,
                model = deviceInfo?.model ?: model,
                imei = it.imei,
                band = deviceInfo?.band,
                deviceOS = deviceInfo?.deviceOS,
                available = it.bookingTime == null,
                bookedBy = it.userName,
                bookingDate = it.bookingTime
            )
        }
    }

    suspend fun bookPhone(imei: Long, userName: String) =
        phoneRepository.findById(imei)?.let {
            if (it.bookingTime != null)
                throw AlreadyBookedException("Phone $imei is already booked")
            it.bookingTime = LocalDateTime.now()
            it.userName = userName
            phoneRepository.save(it)
        }

    suspend fun returnPhone(imei: Long) =
        phoneRepository.findById(imei)?.let {
            it.bookingTime = null
            it.userName = null
            phoneRepository.save(it)
        }
}