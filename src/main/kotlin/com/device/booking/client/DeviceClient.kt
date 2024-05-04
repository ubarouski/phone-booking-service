package com.device.booking.client

import com.device.booking.model.PhoneClientResponse
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class DeviceClient(@Value("\${client.device.url}") private val clientUrl: String) {
    private val client = WebClient.create()

    suspend fun getDevices(brand: String, model: String) =
        client.post()
            .uri("$clientUrl/v1/devices/search?capabilityNames=brand_name,model_name,device_os")
            .contentType(APPLICATION_JSON)
            .bodyValue(listOf(SearchBody("brand_name", brand), SearchBody("model_name", model)))
            .retrieve()
            .onStatus({ it != HttpStatus.OK }) { throw RestClientException("Client Not Available") }
            .awaitBody<List<PhoneClientResponse>>()


    private data class SearchBody(
        @JsonProperty("capability_name")
        val capabilityName: String,
        @JsonProperty("capability_value")
        val capabilityValue: String,
        @JsonProperty("operator")
        val operator: String = "Equal"
    )
}