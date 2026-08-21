package com.payflow.api.dto

import com.payflow.api.domain.Operator
import com.payflow.api.domain.RechargeType
import java.math.BigDecimal

data class UpdateRechargeRequest(
    val amount: BigDecimal,
    val phoneNumber: String,
    val operator: Operator,
    val type: RechargeType
)
