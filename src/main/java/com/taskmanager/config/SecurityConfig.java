// This is a placeholder for SecurityConfig.java
// src/main/java/com/taskmanager/config/SecurityConfig.java
package com.taskmanager.config;

import com.taskmanager.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //         .authorizeHttpRequests(authz -> authz
    //             .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()
    //             .anyRequest().authenticated()
    //         )
    //         .formLogin(form -> form
    //             .loginPage("/login")
    //             .defaultSuccessUrl("/dashboard", true)
    //             .permitAll()
    //         )
    //         .logout(logout -> logout
    //             .logoutUrl("/logout")
    //             .logoutSuccessUrl("/login?logout")
    //             .permitAll()
    //         )
    //         .userDetailsService(userDetailsService);
        
    //     return http.build();
    // }

    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/dashboard", true)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // ✅ this line allows GET logout
            .logoutSuccessUrl("/login?logout")
            .permitAll()
        )
        .userDetailsService(userDetailsService);

    return http.build();
}

    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
