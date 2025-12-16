package com.pm.billingservice.repository;

import com.pm.billingservice.model.Discount;
import com.pm.billingservice.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, DiscountCode> {
}
