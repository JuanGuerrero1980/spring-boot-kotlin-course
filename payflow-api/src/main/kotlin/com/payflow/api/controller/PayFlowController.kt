package com.payflow.api.controller

import com.payflow.api.dto.HelloResponse
import com.payflow.api.dto.StatusResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PayFlowController {

    @GetMapping("/api/hello")
    fun hello(): HelloResponse {
        return HelloResponse(
            message = "Hello from PayFlow API"
        )
    }

    @GetMapping("/api/status")
    fun status(): StatusResponse {
        return StatusResponse(
            status = "UP",
            application = "PayFlow API"
        )
    }
}