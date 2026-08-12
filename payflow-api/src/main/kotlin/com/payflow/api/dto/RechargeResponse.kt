package com.payflow.api.dto

import com.payflow.api.domain.Operator
import com.payflow.api.domain.RechargeStatus
import com.payflow.api.domain.RechargeType
import java.math.BigDecimal

data class RechargeResponse(
    val id: Long,
    val amount: BigDecimal,
    val phoneNumber: String,
    val operator: Operator,
    val type: RechargeType,
    val status: RechargeStatus
)
