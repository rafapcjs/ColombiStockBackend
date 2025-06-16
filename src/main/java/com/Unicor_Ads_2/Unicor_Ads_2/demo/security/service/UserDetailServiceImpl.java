package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.service;





import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.ResourceNotFoundException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.correos.service.IEmailService;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.entity.RoleEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.entity.RoleEnum;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.entity.UserEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.repository.RoleRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.repository.UserRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.dto.AuthResponseDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.dto.ShopkeeperDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.dto.UserCurrentDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload.*;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.util.JwtUtils;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl implements UserDetailsService {


    @Autowired
    private RoleRepository roleRepository; // Inyección explícita

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IEmailService iEmailService;

    @Override
    public UserDetails loadUserByUsername(String username) {

        UserEntity userEntity = userRepository.findUserEntityByUsername(username).orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userEntity.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));


        return new User(userEntity.getUsername(), userEntity.getPassword(), userEntity.isEnabled(), userEntity.isAccountNoExpired(), userEntity.isCredentialNoExpired(), userEntity.isAccountNoLocked(), authorityList);
    }
    @Transactional
    public void activateUser(String dni) {
        UserEntity user = userRepository.findUserEntityByDni(dni)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + dni));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(String dni) {
        UserEntity user = userRepository.findUserEntityByDni(dni)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + dni));
        user.setEnabled(false);
        userRepository.save(user);
    }
    @Transactional
    public AuthResponseDto createUser(AuthCreateUserRequest req) {
        // 1) Normalizar entrada
        String username = req.username().trim().toLowerCase(Locale.ROOT);
        String email    = req.email().trim().toLowerCase(Locale.ROOT);
        String dni      = req.dni().trim();
        String phone    = req.phone().trim();

        // 2) Verificar duplicados
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está en uso");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está en uso");
        }
        if (userRepository.existsByDni(dni)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cédula (DNI) ya está en uso");
        }
        if (userRepository.existsByPhone(phone)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El teléfono ya está en uso");
        }

        // 3) Cargar roles y validar existencia
        Set<RoleEntity> roles = roleRepository
                .findRoleEntitiesByRoleEnumIn(req.roleRequest().roleListName())
                .stream()
                .collect(Collectors.toSet());
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron los roles especificados");
        }

        // 4) Crear y guardar entidad
        UserEntity user = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(req.password()))
                .email(email)
                .name(req.name().trim())
                .lastName(req.lastName().trim())
                .dni(dni)
                .phone(phone)
                .roles(roles)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();
        UserEntity saved = userRepository.save(user);

        // 5) Generar autoridades y token
        List<SimpleGrantedAuthority> authorities = saved.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getRoleEnum().name()))
                .collect(Collectors.toList());
        Authentication auth = new UsernamePasswordAuthenticationToken(saved, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String accessToken = jwtUtils.createToken(auth);

        // 6) Devolver respuesta
        return new AuthResponseDto(
                saved.getUsername(),
                "User created successfully",
                accessToken,
                true
        );
    }

    @Transactional
    public void deleteUserByDni(String dni) {
        UserEntity user = userRepository
                .findUserEntityByDni(dni)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado: " + dni)
                );
        userRepository.delete(user);
    }


    @Transactional
     public AuthResponseDto loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        // 1) Cargar la entidad de usuario y verificar si está habilitado
        UserEntity user = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid username or password")
                );

        if (!user.isEnabled()) {
            // Devuelve 403 Forbidden
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is disabled");
        }

        // 2) Autenticar normalmente
        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);
        return new AuthResponseDto(username,
                "User logged successfully",
                accessToken,
                true);
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException(String.format("Invalid username or password"));
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect Password");
        }

        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
    }
    @Transactional
    public void recoverPassword(String email) {
        UserEntity userEntity = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "El usuario con el correo " + email + " no existe"));

        // Generamos la nueva contraseña
        String newPassword = UUID.randomUUID().toString().substring(0, 10);

        // Editamos la contraseña en la base de datos
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);

        // Enviar correo con la nueva contraseña
        String subject = "Recuperación de contraseña - Colombi Stock";

        String message = """
        <!DOCTYPE html>
        <html lang="es">
        <head>
          <meta charset="UTF-8">
          <title>Recuperación de Contraseña</title>
        </head>
        <body style="margin:0;padding:0;background-color:#f4f4f4;font-family:Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="padding:20px 0;background-color:#f4f4f4;">
            <tr>
              <td align="center">
                <table width="600" cellpadding="0" cellspacing="0"
                       style="background:#ffffff;border-radius:8px;overflow:hidden;
                              box-shadow:0 2px 8px rgba(0,0,0,0.1);">
                  
                  <!-- Header con logo de Colombi Stock -->
                  <tr>
                    <td style="padding:20px;text-align:center;background:#ffffff;">
                      <img src="https://res.cloudinary.com/diaxo8ovb/image/upload/v1745466002/logoSinFondo_rozz1y.png"
                           alt="Colombi Stock" style="width:80px;height:auto;">
                    </td>
                  </tr>
                  
                  <!-- Cuerpo del mensaje -->
                  <tr>
                    <td style="padding:30px;">
                      <h3 style="margin:0 0 15px 0;color:#333333;">
                        ¡Hola, %s! 👋
                      </h3>
                      <p style="margin:0 0 15px 0;line-height:1.6;color:#555555;">
                        Hemos recibido una solicitud para recuperar la contraseña de tu cuenta en Colombi Stock.
                      </p>
                      
                      <p style="margin:0 0 15px 0;line-height:1.6;color:#555555;">
                        <strong>Tu nueva contraseña temporal es:</strong>
                        <code style="background:#f1f1f1;padding:4px 8px;border-radius:4px;
                                    font-family:monospace;color:#333333;">
                          %s
                        </code>
                      </p>

                      <p style="margin:0 0 15px 0;line-height:1.6;color:#555555;">
                        Te recomendamos iniciar sesión con esta clave lo antes posible y cambiarla desde tu perfil
                        para mantener la seguridad de tu cuenta.
                      </p>

                      <p style="margin:0 0 15px 0;line-height:1.6;color:#555555;">
                        Si no solicitaste este cambio, por favor contacta a nuestro equipo de soporte.
                      </p>

                      <p style="margin:0;line-height:1.6;color:#555555;">
                        ¡Gracias por confiar en Colombi Stock!<br>
                        <strong>El equipo de soporte Colombi Stock</strong>
                      </p>
                    </td>
                  </tr>
                  
                  <!-- Footer -->
                  <tr>
                    <td style="background:#f9f9f9;padding:15px;text-align:center;
                               font-size:12px;color:#999999;">
                      © 2025 Colombi Stock. Todos los derechos reservados.
                    </td>
                  </tr>

                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """.formatted(
                userEntity.getUsername(),
                newPassword
        );

        iEmailService.sendEmail(new String[]{ email }, subject, message);
    }



    // Método para cambiar la contraseña
    public void changePassword(ChangePasswordRequest request) {
        // Usamos SecurityUtils para obtener el nombre de usuario
        String username = SecurityUtils.getCurrentUsername();

        // Buscar al usuario en la base de datos
        UserEntity user = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Verificar que la contraseña actual coincide
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Contraseña actual incorrecta");
        }

        // Actualizar y guardar la nueva contraseña
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }


    @Transactional
    public void changePasswordEasy(String dni , String newPassword) {
        // Buscamos al usuario pasando el username desde el request
        UserEntity user = userRepository.findUserEntityByDni(dni)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + dni));

        // Reemplazamos la contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    public UserEntity userInSession() {

        String username = SecurityUtils.getCurrentUsername();


        return  userRepository.findUserEntityByUsername(username).orElseThrow(()-> new UsernameNotFoundException("No existe el usuario con el correo " + username));


    }
    @Transactional(readOnly = true)

    public UserCurrentDto getCurrentUserDto() {
        UserEntity user = userInSession();

        List<String> roleNames = user.getRoles().stream()
                .map(r -> r.getRoleEnum().name())
                .collect(Collectors.toList());
        AuthCreateRoleRequestPayload rolePayload = new AuthCreateRoleRequestPayload(roleNames);

        return new UserCurrentDto(
                user.getUsername(),
                 user.getEmail(),
                user.getName(),
                user.getLastName(),
                user.getDni(),
                user.getPhone(),
                rolePayload
        );
    }


    @Transactional
    public void updateUserInSessionInformation(AuthUpdateUserRequest req) {
        // 1) Obtener usuario en sesión y su entidad
        UserEntity current = userInSession();
        UserEntity entity = userRepository.findUserEntityByUsername(current.getUsername())
                .orElseThrow(() ->
                        new UsernameNotFoundException("No existe el usuario " + current.getUsername())
                );

        // 2) Normalizar entrada
        String newEmail = req.email().trim().toLowerCase(Locale.ROOT);
        String newDni   = req.dni().trim();
        String newPhone = req.phone().trim();
        String newName      = req.name().trim();
        String newLastName  = req.lastName().trim();

        // 3) Validar duplicados (excluyendo el propio registro)
        userRepository.findUserEntityByEmail(newEmail)
                .filter(u -> !u.getUuid().equals(entity.getUuid()))
                .ifPresent(u -> {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "El email ya está en uso"
                    );
                });

        userRepository.findUserEntityByDni(newDni)
                .filter(u -> !u.getUuid().equals(entity.getUuid()))
                .ifPresent(u -> {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "La cédula (DNI) ya está en uso"
                    );
                });

        userRepository.findUserEntityByPhone(newPhone)
                .filter(u -> !u.getUuid().equals(entity.getUuid()))
                .ifPresent(u -> {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "El teléfono ya está en uso"
                    );
                });

        // 4) Actualizar campos
        entity.setEmail(newEmail);
        entity.setName(newName);
        entity.setLastName(newLastName);
        entity.setDni(newDni);
        entity.setPhone(newPhone);

        // 5) Guardar cambios
        userRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<ShopkeeperDto> listShopkeepers() {
        return userRepository
                .findAllByRoles_RoleEnum(RoleEnum.SHOPKEEPER)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ShopkeeperDto toDto(UserEntity u) {
        return new ShopkeeperDto(
                u.getUsername(),
                u.getEmail(),
                u.getDni(),
                u.getPhone(),
                u.getLastName(),
                u.isEnabled()
        );
    }




}