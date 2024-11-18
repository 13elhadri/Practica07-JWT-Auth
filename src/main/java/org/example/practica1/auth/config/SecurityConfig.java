package org.example.practica1.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity // Habilitamos la seguridad
@EnableMethodSecurity // Habilitamos la seguridad a nivel de método
public class SecurityConfig {
    // private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${api.version}")
    private String apiVersion;

    @Autowired
    public SecurityConfig(UserDetailsService userService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userService = userService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(request -> request.requestMatchers("/error/**").permitAll())

                .authorizeHttpRequests(request -> request.requestMatchers("/images/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/webjars/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/funkos/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/ws/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/storage/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/" + apiVersion + "/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/**").authenticated())

                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }


}