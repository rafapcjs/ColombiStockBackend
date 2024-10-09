package com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SupplierPayload {

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres.")
    private String name;

    @NotBlank(message = "El apellido no puede estar vacío.")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres.")
    private String lastName;

    @NotBlank(message = "El DNI no puede estar vacío.")
    @Size(max = 20, message = "El DNI no puede exceder los 20 caracteres.")
    private String dni;

    @NotBlank(message = "El número de contacto no puede estar vacío.")
    @Size(max = 15, message = "El número de contacto no puede exceder los 15 caracteres.")
    private String phone;

    @Email(message = "El formato del correo electrónico no es válido.")
    @Size(max = 100, message = "El correo electrónico no puede exceder los 100 caracteres.")
    private String email;


}
