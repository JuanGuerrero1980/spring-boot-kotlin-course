package com.payflow.api.repository

import com.payflow.api.domain.Recharge
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
class RechargeRepository {

    private val storage = ConcurrentHashMap<Long, Recharge>()
    private val idGenerator = AtomicLong(1)

    fun save(recharge: Recharge): Recharge {
        val id = idGenerator.getAndIncrement()
        val persisted = Recharge(
            id = id,
            amount = recharge.amount,
            phoneNumber = recharge.phoneNumber,
            operator = recharge.operator,
            type = recharge.type,
            status = recharge.status
        )
        storage[id] = persisted
        return persisted
    }

    fun findById(id: Long): Recharge? {
        return storage[id]
    }
}
