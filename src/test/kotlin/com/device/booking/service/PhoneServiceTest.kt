package com.device.booking.service

import com.device.booking.client.DeviceClient
import com.device.booking.dto.PhoneBooking
import com.device.booking.exceptions.AlreadyBookedException
import com.device.booking.model.DeviceInfo
import com.device.booking.model.Phone
import com.device.booking.model.PhoneClientResponse
import com.device.booking.repository.DeviceRepository
import com.device.booking.repository.PhoneRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.client.RestClientException
import java.time.LocalDateTime


@ExtendWith(MockKExtension::class)
internal class PhoneServiceTest {

    @MockK
    private lateinit var phoneRepository: PhoneRepository

    @MockK
    private lateinit var deviceRepository: DeviceRepository

    @MockK
    private lateinit var deviceClient: DeviceClient

    @InjectMockKs
    private lateinit var phoneService: PhoneService

    @Test
    fun `should find booked phones`() {
        val expectedResult = listOf(PhoneBooking(BRAND, MODEL, IMEI, null, OS, false, USER_NAME, timestamp))
        val phone = mockk<Phone>()
        val phoneClientResponse = mockk<PhoneClientResponse>(relaxed = true)
        coEvery { phoneRepository.findPhones(any(), any()) } returns listOf(phone).asFlow()
        coEvery { deviceClient.getDevices(any(), any()) } returns listOf(phoneClientResponse)
        every { phoneClientResponse.capabilities.brand } returns BRAND
        every { phoneClientResponse.capabilities.model } returns MODEL
        every { phoneClientResponse.capabilities.deviceOS } returns OS
        every { phone.imei } returns IMEI
        every { phone.userName } returns USER_NAME
        every { phone.bookingTime } returns timestamp

        val result = runBlocking { phoneService.findPhones(BRAND, MODEL).toList() }

        assertThat(result).isEqualTo(expectedResult)
        coVerify { phoneRepository.findPhones(BRAND, MODEL) }
        coVerify { deviceClient.getDevices(BRAND, MODEL) }
        coVerify(exactly = 0) { deviceRepository.findAllByBrandAndModel(any(), any()) }
        confirmVerified(phoneRepository, deviceClient, deviceRepository)
    }

    @Test
    fun `should find available for booking phone when device service error`() {
        val expectedResult = listOf(PhoneBooking(BRAND, MODEL, IMEI, BAND, OS, true))
        val phone = mockk<Phone>()
        val deviceInfo = mockk<DeviceInfo>()
        coEvery { phoneRepository.findPhones(any(), any()) } returns listOf(phone).asFlow()
        coEvery { deviceClient.getDevices(any(), any()) } throws RestClientException("Service exception")
        coEvery { deviceRepository.findAllByBrandAndModel(any(), any()) } returns listOf(deviceInfo).asFlow()
        every { phone.imei } returns IMEI
        every { phone.userName } returns null
        every { phone.bookingTime } returns null
        every { deviceInfo.model } returns MODEL
        every { deviceInfo.brand } returns BRAND
        every { deviceInfo.band } returns BAND
        every { deviceInfo.deviceOS } returns OS

        val result = runBlocking { phoneService.findPhones(BRAND, MODEL).toList() }

        assertThat(result).isEqualTo(expectedResult)
        coVerify { phoneRepository.findPhones(BRAND, MODEL) }
        coVerify { deviceClient.getDevices(BRAND, MODEL) }
        coVerify { deviceRepository.findAllByBrandAndModel(BRAND, MODEL) }
        confirmVerified(phoneRepository, deviceClient, deviceRepository)
    }

    @Test
    fun `should book phone`() {
        val bookingTime = slot<LocalDateTime>()
        val userName = slot<String>()
        val phone = mockk<Phone>()
        coEvery { phoneRepository.findById(any()) } returns phone
        every { phone.bookingTime } returns null
        justRun { phone.bookingTime = capture(bookingTime) }
        justRun { phone.userName = USER_NAME }
        coEvery { phoneRepository.save(any()) } returns phone

        val result = runBlocking { phoneService.bookPhone(IMEI, USER_NAME) }

        coVerify { phoneRepository.findById(IMEI) }
        coVerify { phoneRepository.save(phone) }
        verify { phone getProperty "bookingTime" }
        verify { phone setProperty "bookingTime" value bookingTime.captured }
        verify { phone setProperty "userName" value USER_NAME }
        confirmVerified(phoneRepository, phone)
    }

    @Test
    fun `should not book phone`() {

        val phone = mockk<Phone>()
        coEvery { phoneRepository.findById(any()) } returns phone
        every { phone.bookingTime } returns LocalDateTime.now()

        assertThrows(AlreadyBookedException::class.java) { runBlocking { phoneService.bookPhone(IMEI, USER_NAME) } }

        coVerify { phoneRepository.findById(IMEI) }
        coVerify(exactly = 0) { phoneRepository.save(phone) }
        confirmVerified(phoneRepository)
    }

    @Test
    fun `should return phone`() {
        val phone = mockk<Phone>()
        coEvery { phoneRepository.findById(any()) } returns phone
        every { phone.bookingTime } returns null
        justRun { phone.bookingTime = null }
        justRun { phone.userName = null }
        coEvery { phoneRepository.save(any()) } returns phone

        val result = runBlocking { phoneService.returnPhone(IMEI) }

        coVerify { phoneRepository.findById(IMEI) }
        coVerify { phoneRepository.save(phone) }
        verify { phone setProperty "bookingTime" value null }
        verify { phone setProperty "userName" value null }
        confirmVerified(phoneRepository, phone)
    }

    companion object {
        private const val BRAND = "BRAND"
        private const val MODEL = "MODEL"
        private const val BAND = "2g"
        private const val OS = "OS"
        private const val IMEI = 111111111111111L
        private const val USER_NAME = "User Name"
        private val timestamp = LocalDateTime.now()
    }
}