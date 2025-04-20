package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.controller;


import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.dto.AuthResponseDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload.AuthCreateUserRequest;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload.AuthLoginRequest;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload.ChangePasswordRequest;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.service.UserDetailServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants;

import org.springframework.web.bind.annotation.*;

/**
 * Controlador para la autenticación y gestión de usuarios.
 * Proporciona los endpoints necesarios para el registro de usuarios, inicio de sesión, recuperación de contraseñas,
 * y cambio de contraseñas.
 */
@RestController
@Tag(name = "Autenticación", description = "Endpoints para el registro, inicio de sesión y gestión de contraseñas")
public class AuthenticationController {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    /**
     * Endpoint para registrar un nuevo usuario.
     *
     * @param userRequest Información del usuario a registrar.
     * @return Respuesta con los detalles del usuario creado.
     */
    @PostMapping(EndpointsConstants.ENDPOINT_SIGN_UP)
    @Operation(
            summary = "Registrar usuario",
            description = "Registra un nuevo usuario en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida.")
            }
    )
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid AuthCreateUserRequest userRequest) {
        return new ResponseEntity<>(this.userDetailService.createUser(userRequest), HttpStatus.CREATED);
    }

    /**
     * Endpoint para iniciar sesión con credenciales válidas.
     * Devuelve un token JWT si las credenciales son correctas.
     *
     * @param userRequest Credenciales del usuario.
     * @return Respuesta con el token JWT.
     */
    @PostMapping(EndpointsConstants.ENDPOINT_LOGIN)
    @Operation(
            summary = "Iniciar sesión",
            description = "Permite a un usuario autenticarse en la plataforma mediante credenciales válidas. Devuelve un token JWT si las credenciales son correctas.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Inicio de sesión exitoso. Devuelve el token JWT.",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida (credenciales mal formadas)."),
                    @ApiResponse(responseCode = "401", description = "No autorizado. Credenciales incorrectas.")
            }
    )
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthLoginRequest userRequest) {
        return new ResponseEntity<>(this.userDetailService.loginUser(userRequest), HttpStatus.OK);
    }

    /**
     * Endpoint para recuperar la contraseña de un usuario por correo electrónico.
     *
     * @param email Correo electrónico del usuario.
     * @return Respuesta indicando que se ha enviado una nueva contraseña.
     */
    @PostMapping(EndpointsConstants.ENDPOINT_RECOVER_PASSWORD)
    @Operation(
            summary = "Recuperar contraseña",
            description = "Recupera la contraseña de un usuario enviando una nueva contraseña temporal a su correo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Se ha enviado una nueva contraseña temporal al correo."),
                    @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el correo proporcionado.")
            }
    )
    public ResponseEntity<String> recoverPassword(@RequestParam String email) {
        userDetailService.recoverPassword(email);
        return ResponseEntity.ok("Se ha enviado una nueva contraseña temporal a tu correo.");
    }

    /**
     * Endpoint para cambiar la contraseña de un usuario autenticado.
     *
     * @param request Contiene la contraseña actual y la nueva contraseña.
     * @return Respuesta indicando que la contraseña se ha cambiado correctamente.
     */
    @PutMapping(EndpointsConstants.ENDPOINT_CHANGE_PASSWORD)
    @Operation(
            summary = "Cambiar contraseña",
            description = "Permite a un usuario cambiar su contraseña actual por una nueva.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Contraseña cambiada exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida o contraseña actual incorrecta."),
                    @ApiResponse(responseCode = "401", description = "No autorizado.")
            }
    )
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {

        userDetailService.changePassword( request);
        return ResponseEntity.noContent().build();
    }
}
