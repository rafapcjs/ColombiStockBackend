package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.controller;


import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.entity.UserEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.dto.*;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload.AuthCreateUserRequest;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload.AuthLoginRequest;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload.AuthUpdateUserRequest;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload.ChangePasswordRequest;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.service.UserDetailServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants;

import java.util.List;

import static com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants.*;

/**
 * Controlador para la autenticación y gestión de usuarios.
 * Proporciona los endpoints necesarios para el registro de usuarios, inicio de sesión, recuperación de contraseñas,
 * y cambio de contraseñas.
 */
@RestController
@Tag(name = "Autenticación y Admnistracion de cuentas", description = "Endpoints para el registro, inicio de sesión y gestión de contraseñas")
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



    @PutMapping(ENDPOINT_UPDATE_INFO_USER)
    @Operation(
            summary = "actualizar usuario",
            description = "actualizar usuario en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario actualizado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida.")
            }
    )
    public ResponseEntity<AuthUpdateUserRequest> udpate(@RequestBody @Valid AuthUpdateUserRequest userRequest) {

        this.userDetailService.updateUserInSessionInformation(userRequest);

        return ResponseEntity.noContent().build();
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

    @PostMapping(EndpointsConstants.ENDPOINT_RECOVER_PASSWORD)
    @Operation(
            summary = "Recuperar contraseña",
            description = "Recibe un JSON con el email y envía una contraseña temporal.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Se ha enviado la nueva contraseña."),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
            }
    )
    public ResponseEntity<String> recoverPassword(@RequestBody RecoverPasswordDto req) {
        userDetailService.recoverPassword(req.email());
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


    @Operation(
            summary = "Obtener datos del usuario autenticado",
            description = "Devuelve la información del usuario actualmente autenticado en sesión.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario recuperado correctamente",
                            content = @Content (
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserCurrentDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "No estás autenticado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @GetMapping(ENDPOINT_ME)
    public ResponseEntity<UserCurrentDto> whoAmI() {
        UserCurrentDto dto = userDetailService.getCurrentUserDto();
        return ResponseEntity.ok(dto);
    }


    @GetMapping(value=ENDPOINT_CLOSED_AUTH)
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext(); // limpia la sesión en este hilo
        return ResponseEntity.ok().build();   // 200 OK sin cuerpo
    }


    @PutMapping(ENDPOINT_CHANGE_PASSWORD_EASY +"/{dni}")
     public ResponseEntity<Void> changePassword(
            @PathVariable("dni") String dni,
            @RequestBody ChangePasswordEasyRequest changePasswordRequest ) {

        userDetailService.changePasswordEasy(dni,changePasswordRequest.newPassword());
        return ResponseEntity.noContent().build();
    }
    @GetMapping(ENDPOINT_GETALL_SHOPKEEPR)
    public ResponseEntity<List<ShopkeeperDto>> listShopkeepers() {
        List<ShopkeeperDto> shopkeepers = userDetailService.listShopkeepers();
        return ResponseEntity.ok(shopkeepers);
    }

    @PutMapping(ENDPOINT_ACTIVEACCES+"/{dni}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable String dni) {
        userDetailService.activateUser(dni);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/users/{dni}/deactivate
     * Desactiva al usuario con el DNI dado.
     */
    @PutMapping(ENDPOINT_ACTIVEACCES+"/{dni}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable String dni) {
        userDetailService.deactivateUser(dni);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(ENDPOINT_AUTH_BASE+"/delete"+"/{dni}")
    public ResponseEntity<Void> deleteUser(@PathVariable String dni) {
        userDetailService.deleteUserByDni(dni);
        return ResponseEntity.noContent().build();
    }

}
