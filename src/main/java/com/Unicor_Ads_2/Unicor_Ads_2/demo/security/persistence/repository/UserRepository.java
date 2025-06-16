package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.repository;

 import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.entity.RoleEnum;
 import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.entity.UserEntity;
 import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

 import java.util.List;
 import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findUserEntityByEmail(String email);  // Este es el método que necesitas
    Optional<UserEntity> findUserEntityByDni(String dni);
    Optional<UserEntity> findUserEntityByPhone(String phone);

    Optional<UserEntity> findUserEntityByUsername(String username);
    boolean existsByUsername(String username);
    List<UserEntity> findAllByRoles_RoleEnum(RoleEnum roleEnum);

    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

}
