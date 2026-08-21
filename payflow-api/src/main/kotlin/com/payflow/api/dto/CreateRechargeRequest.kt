package com.payflow.api.dto

import com.payflow.api.domain.Operator
import com.payflow.api.domain.RechargeType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CreateRechargeRequest(
    @field:Positive
    val amount: BigDecimal,

    @field:NotBlank
    val phoneNumber: String,

    @field:NotNull
    val operator: Operator,

    @field:NotNull
    val type: RechargeType
)
