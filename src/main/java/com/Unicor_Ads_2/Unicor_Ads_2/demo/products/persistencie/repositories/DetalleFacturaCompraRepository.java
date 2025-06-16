package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.repositories;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.DetalleFacturaCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Deta@lleFacturaCompraRepository.java
public interface DetalleFacturaCompraRepository extends JpaRepository<DetalleFacturaCompra, String> {}
