package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.repository;

 import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.entity.UserEntity;
 import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findUserEntityByEmail(String email);  // Este es el método que necesitas

    Optional<UserEntity> findUserEntityByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

}
