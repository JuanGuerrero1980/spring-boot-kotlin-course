package com.payflow.api.repository

import com.payflow.api.domain.Recharge
import com.payflow.api.domain.RechargeStatus
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

    fun findAll(): List<Recharge> {
        return storage.values.toList()
    }

    fun updateStatus(id: Long, status: RechargeStatus): Recharge? {
        val existing = storage[id] ?: return null

        val updated = Recharge(
            id = existing.id,
            amount = existing.amount,
            phoneNumber = existing.phoneNumber,
            operator = existing.operator,
            type = existing.type,
            status = status
        )

        storage[id] = updated
        return updated
    }

    fun update(id: Long, recharge: Recharge): Recharge? {
        if (!storage.containsKey(id)) {
            return null
        }

        storage[id] = recharge
        return recharge
    }

    fun delete(id: Long): Boolean {
        return storage.remove(id) != null
    }
}
