package com.payflow.api.dto

import com.payflow.api.domain.RechargeStatus

data class PatchRechargeRequest(
    val status: RechargeStatus
)
