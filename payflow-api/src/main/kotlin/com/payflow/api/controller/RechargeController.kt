package com.payflow.api.controller

import com.payflow.api.dto.CreateRechargeRequest
import com.payflow.api.dto.RechargeResponse
import com.payflow.api.service.RechargeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/recharges")
class RechargeController(
    private val rechargeService: RechargeService
) {

    @PostMapping
    fun create(
        @RequestBody request: CreateRechargeRequest
    ): ResponseEntity<RechargeResponse> {
        val response = rechargeService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Long
    ): ResponseEntity<RechargeResponse> {
        val response = rechargeService.findById(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(response)
    }
}
