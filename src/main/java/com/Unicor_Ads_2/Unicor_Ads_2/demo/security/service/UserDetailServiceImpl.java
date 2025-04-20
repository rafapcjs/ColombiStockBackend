package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.service;





import com.Unicor_Ads_2.Unicor_Ads_2.demo.correos.service.IEmailService;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.entity.RoleEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.entity.UserEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.repository.RoleRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.repository.UserRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.dto.AuthResponseDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload.AuthCreateUserRequest;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload.AuthLoginRequest;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload.ChangePasswordRequest;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.util.JwtUtils;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

    public AuthResponseDto createUser(AuthCreateUserRequest createUserRequest) {

        String username = createUserRequest.username();
        String password = createUserRequest.password();
        String email = createUserRequest.email();        // Obtener email
        String name = createUserRequest.name();          // Obtener nombre
        String lastName = createUserRequest.lastName();  // Obtener apellido
        String dni = createUserRequest.dni();            // Obtener DNI
        String phone = createUserRequest.phone();        // Obtener teléfono
        List<String> rolesRequest = createUserRequest.roleRequest().roleListName(); // Obtener roles

        // Buscar roles en la base de datos según los nombres proporcionados
        Set<RoleEntity> roleEntityList = roleRepository.findRoleEntitiesByRoleEnumIn(rolesRequest).stream()
                .collect(Collectors.toSet());

        // Verificar si los roles existen
        if (roleEntityList.isEmpty()) {
            throw new IllegalArgumentException("The roles specified do not exist.");
        }

        // Crear la entidad de usuario con los nuevos datos
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))  // Codificar la contraseña
                .email(email)                                // Establecer el email
                .name(name)                                  // Establecer el nombre
                .lastName(lastName)                          // Establecer el apellido
                .dni(dni)                                    // Establecer el DNI
                .phone(phone)                                // Establecer el teléfono
                .roles(roleEntityList)                       // Establecer los roles
                .isEnabled(true)                             // Establecer que está habilitado por defecto
                .accountNoLocked(true)                       // Establecer que la cuenta no está bloqueada
                .accountNoExpired(true)                      // Establecer que la cuenta no ha expirado
                .credentialNoExpired(true)                   // Establecer que las credenciales no han expirado
                .build();

        // Guardar la entidad de usuario en la base de datos
        UserEntity userSaved = userRepository.save(userEntity);

        // Crear lista de autoridades para el JWT
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        userSaved.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        // Crear autenticación y establecer el contexto de seguridad
        SecurityContext securityContextHolder = SecurityContextHolder.getContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userSaved, null, authorities);

        // Crear token JWT
        String accessToken = jwtUtils.createToken(authentication);

        // Crear la respuesta con el token y los datos del usuario
        AuthResponseDto authResponse = new AuthResponseDto(username, "User created successfully", accessToken, true);
        return authResponse;
    }


    public AuthResponseDto loginUser(AuthLoginRequest authLoginRequest) {

        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);
        AuthResponseDto authResponse = new AuthResponseDto(username, "User loged succesfully", accessToken, true);
        return authResponse;
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
    public void recoverPassword(String email) {
        UserEntity userEntity = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario con el correo " + email + " no existe"));

        // Generamos la nueva contraseña
        String newPassword = UUID.randomUUID().toString().substring(0, 10);

        // Editamos la contraseña en la base de datos
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);

        // Enviar correo con la nueva contraseña
        String subject = "Recuperación de contraseña";
        String message = """
    <html>
        <body>
            <img src="https://res.cloudinary.com/diaxo8ovb/image/upload/v1745181321/images_usldgt.png" alt="Logo" width="150" height="150" />
            <h3>¡Hola, %s! 👋</h3>
            <p>Hemos recibido una solicitud para recuperar la contraseña de tu cuenta.</p>
            <p><b>Tu nueva contraseña temporal es: </b><code>%s</code></p>
            <p>Te recomendamos iniciar sesión con esta clave lo antes posible y cambiarla desde tu perfil para mantener la seguridad de tu cuenta.</p>
            <p>Si no solicitaste este cambio, por favor contacta a nuestro equipo de soporte.</p>
            <p>¡Gracias por confiar en nosotros!</p>
            <p>Saludos,<br />El equipo de soporte.</p>
        </body>
    </html>
    """.formatted(userEntity.getUsername(), newPassword);


        iEmailService.sendEmail(new String[]{email}, subject, message);
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

}