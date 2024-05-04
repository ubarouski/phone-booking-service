package com.device.booking.model

import com.fasterxml.jackson.annotation.JsonProperty


data class PhoneClientResponse(
    @JsonProperty("capabilities")
    val capabilities: Capabilities

)

data class Capabilities(
    @JsonProperty("device_os")
    val deviceOS: String,
    @JsonProperty("brand_name")
    val brand: String,
    @JsonProperty("model_name")
    val model: String,
)
