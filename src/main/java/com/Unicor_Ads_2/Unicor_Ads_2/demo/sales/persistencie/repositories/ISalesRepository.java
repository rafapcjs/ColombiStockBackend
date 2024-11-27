package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.repositories;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISalesRepository extends JpaRepository<Sales, UUID> {

    Optional<Sales>findByUuid(UUID uuid);
}
