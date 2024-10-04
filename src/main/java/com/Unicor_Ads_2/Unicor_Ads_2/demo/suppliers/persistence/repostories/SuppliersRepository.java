package com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.repostories;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.entities.Suppliers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SuppliersRepository extends JpaRepository<Suppliers, UUID> {
}
