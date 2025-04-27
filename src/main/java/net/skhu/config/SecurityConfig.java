package net.skhu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled=true)
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/oauth2/**", "/login/**").permitAll()
                .anyRequest().permitAll()
            )
            .formLogin((form) -> form
                .loginPage("/login")
                .loginProcessingUrl("/login_processing")
                .failureUrl("/login?error")
                .defaultSuccessUrl("/", true)
                .usernameParameter("loginName")
                .passwordParameter("password")
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout_processing"))
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .permitAll())
            .oauth2Login(oauth2 -> oauth2
                    .loginPage("/login")
                    .defaultSuccessUrl("/oauth2signup"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
