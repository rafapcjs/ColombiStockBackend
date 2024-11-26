package com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.entities;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.entities.BaseEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "suppliers")
public class Suppliers extends BaseEntity {

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres.")
    private String name;

    @Column(name = "last_name")
    @NotBlank(message = "El apellido no puede estar vacío.")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres.")
    private String lastName;

    @NotBlank(message = "El DNI no puede estar vacío.")
    @Size(max = 20, message = "El DNI no puede exceder los 20 caracteres.")
    @Pattern(regexp = "^[0-9]+$", message = "El DNI debe contener solo números.")
    @Column(unique = true, nullable = false) // Asegurar que el DNI sea único
    private String dni;

    @NotBlank(message = "El número de contacto no puede estar vacío.")
    @Size(max = 15, message = "El número de contacto no puede exceder los 15 caracteres.")
    @Column(unique = true, nullable = false) // Asegurar que el teléfono sea único
    private String phone;

    @Email(message = "El formato del correo electrónico no es válido.")
    @Size(max = 100, message = "El correo electrónico no puede exceder los 100 caracteres.")
    private String email;

    @OneToMany(mappedBy = "suppliers")
    private List<Products> productsList;
}
