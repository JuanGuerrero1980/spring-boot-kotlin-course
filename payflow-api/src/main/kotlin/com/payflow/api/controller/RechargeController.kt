package com.payflow.api.controller

import com.payflow.api.dto.CreateRechargeRequest
import com.payflow.api.dto.PatchRechargeRequest
import com.payflow.api.dto.RechargeResponse
import com.payflow.api.dto.UpdateRechargeRequest
import com.payflow.api.service.RechargeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: Long,
        @RequestBody request: PatchRechargeRequest
    ): ResponseEntity<RechargeResponse> {
        val response = rechargeService.patch(id, request)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun findAll(): ResponseEntity<List<RechargeResponse>> {
        val response = rechargeService.findAll()
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun replace(
        @PathVariable id: Long,
        @RequestBody request: UpdateRechargeRequest
    ): ResponseEntity<RechargeResponse> {
        val response = rechargeService.update(id, request)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long
    ): ResponseEntity<Unit> {
        val response = rechargeService.delete(id)

        return if (response) {
            ResponseEntity.noContent().build()
        } else ResponseEntity.notFound().build()
    }
}
