package com.consentmanagement.repository

import com.consentmanagement.entity.TargetCustomer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface TargetCustomerRepository : JpaRepository<TargetCustomer, UUID> {
    fun findByCustomerId(customerId: String): Optional<TargetCustomer>
}
