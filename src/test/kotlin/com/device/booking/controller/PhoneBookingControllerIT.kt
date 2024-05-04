package com.device.booking.controller

import com.device.booking.dto.Action
import com.device.booking.dto.BookingRequestBody
import com.device.booking.dto.PhoneBooking
import com.device.booking.model.Phone
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

@SpringBootTest
@ActiveProfiles("test")
internal class PhoneBookingControllerIT(context: ApplicationContext) {
    private val client = WebTestClient.bindToApplicationContext(context).build()

    @Test
    fun `should return phone booking`() {
        val result = client.get().uri("/v1/phones?brand=Nokia&model=3310").accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList<PhoneBooking>().returnResult().responseBody

        assertThat(result).hasSize(1).isEqualTo(listOf(PhoneBooking("Nokia", "3310", 355623112522310, null, "x", true)))
    }

    @Test
    fun `should book phone and return`() {
        var result = client.post().uri("/v1/phones/355623112522301").accept(MediaType.APPLICATION_JSON)
            .bodyValue(BookingRequestBody("User Name", Action.BOOK))
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList<Phone>().returnResult().responseBody

        assertThat(result).hasSize(1)
        assertThat(result!!.first().bookingTime).isNotNull()
        assertThat(result.first().userName).isEqualTo("User Name")

        result = client.post().uri("/v1/phones/355623112522301").accept(MediaType.APPLICATION_JSON)
            .bodyValue(BookingRequestBody(action = Action.RETURN))
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList<Phone>().returnResult().responseBody

        assertThat(result).hasSize(1)
        assertThat(result!!.first().bookingTime).isNull()
        assertThat(result.first().userName).isNull()
    }

    @Test
    fun `should not book phone twice`() {
        val result = client.post().uri("/v1/phones/355623112522302").accept(MediaType.APPLICATION_JSON)
            .bodyValue(BookingRequestBody("User Name", Action.BOOK))
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList<Phone>().returnResult().responseBody

        assertThat(result).hasSize(1)
        assertThat(result!!.first().bookingTime).isNotNull()
        assertThat(result.first().userName).isEqualTo("User Name")

        val errorResult = client.post().uri("/v1/phones/355623112522302").accept(MediaType.APPLICATION_JSON)
            .bodyValue(BookingRequestBody("User Name", Action.BOOK))
            .exchange()
            .expectStatus().is4xxClientError()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBodyList<ProblemDetail>().returnResult().responseBody

        assertThat(errorResult).hasSize(1)
        assertThat(errorResult!!.first().status).isEqualTo(HttpStatus.CONFLICT.value())
    }


    companion object {
        private lateinit var mockBackEnd: MockWebServer

        @DynamicPropertySource
        @JvmStatic
        fun setProperties(registry: DynamicPropertyRegistry) {
            mockBackEnd = MockWebServer()
            mockBackEnd.start()
            val url = mockBackEnd.url("/")
            registry.add("client.device.url") { "$url" }

            mockBackEnd.dispatcher = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    return MockResponse().setBody("[{\"primary\":true,\"capabilities\":{\"brand_name\":\"Nokia\",\"device_os\":\"x\",\"model_name\":\"3310\"},\"wurfl_id\":\"nokia_3310_ver1\",\"secondary_wurflids\":[\"nokia_3310_ver1_subua3g\",\"nokia_3310_ver1_subuaoperamini\"]}]")
                        .addHeader("Content-Type", "application/json")
                }
            }
        }

        @JvmStatic
        @AfterAll
        fun teardown() {
            mockBackEnd.shutdown()
        }
    }
}