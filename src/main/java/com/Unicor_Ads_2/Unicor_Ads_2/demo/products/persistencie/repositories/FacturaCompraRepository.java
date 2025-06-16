package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.repositories;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.FacturaCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
// FacturaCompraRepository.java
public interface FacturaCompraRepository extends JpaRepository<FacturaCompra, String> {
    Optional<FacturaCompra> findByNumeroFactura(String numeroFactura);
}
