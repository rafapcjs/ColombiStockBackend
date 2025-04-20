package com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.initializer;


import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.entity.RoleEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.entity.RoleEnum;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Crear roles si no existen y forzar el flush para persistirlos inmediatamente
        createRoleIfNotFound(RoleEnum.ADMIN);
        createRoleIfNotFound(RoleEnum.SHOPKEEPER);

        // Crear el docente ya que ahora el rol TEACHER existe y está gestionado
     }

    private void createRoleIfNotFound(RoleEnum roleEnum) {
        if (!roleRepository.existsByRoleEnum(roleEnum)) {
            RoleEntity roleEntity = RoleEntity.builder()
                    .roleEnum(roleEnum)
                    .build();
            // Guardamos y forzamos el flush para que quede en el contexto de persistencia
            roleRepository.saveAndFlush(roleEntity);
            System.out.println("Role created: " + roleEnum.name());
        } else {
            System.out.println("Role already exists: " + roleEnum.name());
        }
    }
}