package com.pm.billingservice.repository;

import com.pm.billingservice.model.BillingAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface BillingAccountRepository extends JpaRepository<BillingAccount, UUID> {
    boolean existsByPatientId(String patientId);

    @Query(
            value = """
              select * from billing_account ba
              where ba.account_status = 'ACTIVE'
                and (
                  (ba.billing_cadence = 'MONTHLY'  and coalesce(ba.last_invoiced_end, ba.cycle_anchor) + interval '1 month' <= :today)
                  or
                  (ba.billing_cadence = 'ANNUALLY' and coalesce(ba.last_invoiced_end, ba.cycle_anchor) + interval '1 year'<= :today)
                )
              ORDER BY ba.id
              """,
            countQuery = """
            
            select count(*) from billing_account ba
              where ba.account_status = 'ACTIVE'
                and (
                  (ba.billing_cadence = 'MONTHLY'  and coalesce(ba.last_invoiced_end, ba.cycle_anchor) + interval '1 month' <= :today)
                  or
                  (ba.billing_cadence = 'ANNUALLY' and coalesce(ba.last_invoiced_end, ba.cycle_anchor) + interval '1 year'<= :today)
                )
              """,
            nativeQuery = true
    )
    Page<BillingAccount> findDueAccounts(@Param("today") LocalDate today, Pageable pageable);

}
