package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.security;


 import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.security.filter.JwtTokenValidator;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.service.UserDetailServiceImpl;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

 import static com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants.ENDPOINT_LOGIN;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider
    ) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Registra aquí tu proveedor para autenticación si usas BasicAuth o formLogin
                .authenticationProvider(authenticationProvider)
                // Inserta tu filtro JWT antes de que se intente cualquier autenticación “normal”
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas:
                        .requestMatchers(HttpMethod.POST  ,ENDPOINT_LOGIN)
                        .permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/v3/api-docs.json",
                                "/"
                        ).permitAll()



                        // **Aquí** exiges autenticación para todo lo demás:
                        .anyRequest().permitAll()
                )
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
