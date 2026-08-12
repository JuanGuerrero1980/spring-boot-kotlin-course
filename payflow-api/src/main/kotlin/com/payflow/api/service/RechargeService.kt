package com.payflow.api.service

import com.payflow.api.domain.Recharge
import com.payflow.api.domain.RechargeStatus
import com.payflow.api.dto.CreateRechargeRequest
import com.payflow.api.dto.RechargeResponse
import com.payflow.api.repository.RechargeRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class RechargeService(
    private val rechargeRepository: RechargeRepository
) {

    fun create(request: CreateRechargeRequest): RechargeResponse {
        require(request.amount > BigDecimal.ZERO) {
            "Amount must be greater than zero"
        }

        require(request.phoneNumber.isNotBlank()) {
            "Phone number is required"
        }

        val recharge = Recharge(
            id = 0,
            amount = request.amount,
            phoneNumber = request.phoneNumber,
            operator = request.operator,
            type = request.type,
            status = RechargeStatus.PENDING
        )

        val saved = rechargeRepository.save(recharge)
        return saved.toResponse()
    }

    fun findById(id: Long): RechargeResponse? {
        return rechargeRepository.findById(id)?.toResponse()
    }

    private fun Recharge.toResponse(): RechargeResponse {
        return RechargeResponse(
            id = id,
            amount = amount,
            phoneNumber = phoneNumber,
            operator = operator,
            type = type,
            status = status
        )
    }
}
