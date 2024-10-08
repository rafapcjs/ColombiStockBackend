package com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.repostories;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.entities.Suppliers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SuppliersRepository extends JpaRepository<Suppliers, UUID> {


    Optional<Suppliers> findSuppliersByEmailIgnoreCase(String email);

    Optional<Suppliers> findSuppliersByPhoneIgnoreCase(String phone);

    Optional<Suppliers> findSuppliersByDniIgnoreCase(String dni);

    Page<Suppliers> findAll(Pageable pageable);

    void deleteSuppliersByDni(String dni);

    Optional<Suppliers> findSuppliersByNameIgnoreCase(String name);
}
