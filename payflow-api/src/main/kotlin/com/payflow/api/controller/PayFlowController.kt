package com.payflow.api.controller

import com.payflow.api.dto.HelloResponse
import com.payflow.api.dto.StatusResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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

    @GetMapping("/api/headers")
    fun headers(
        request: HttpServletRequest,
        @RequestHeader("X-Request-Id", required = false) requestId: String?
    ): ResponseEntity<Map<String, String?>> {
        val responseBody = mapOf(
            "method" to request.method,
            "path" to request.requestURI,
            "contentType" to request.contentType,
            "receivedRequestId" to requestId
        )

        return ResponseEntity
            .ok()
            .header("X-Response-Id", requestId ?: UUID.randomUUID().toString())
            .body(responseBody)
    }
}
