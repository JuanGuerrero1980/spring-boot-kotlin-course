package com.payflow.api.domain

import java.math.BigDecimal

class Recharge(
    val id: Long,
    val amount: BigDecimal,
    val phoneNumber: String,
    val operator: Operator,
    val type: RechargeType,
    val status: RechargeStatus
)
