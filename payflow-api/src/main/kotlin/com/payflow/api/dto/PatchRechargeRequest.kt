package com.payflow.api.dto

import com.payflow.api.domain.RechargeStatus
import jakarta.validation.constraints.NotNull

data class PatchRechargeRequest(
    @field:NotNull
    val status: RechargeStatus
)
