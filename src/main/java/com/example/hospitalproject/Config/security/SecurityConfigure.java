package com.example.hospitalproject.Config.security;

import com.example.hospitalproject.Config.jwt.JwtAccessDeniedHandler;
import com.example.hospitalproject.Config.jwt.JwtAuthenticationEntryPointHandler;
import com.example.hospitalproject.Config.jwt.JwtSecurityConfig;
import com.example.hospitalproject.Config.jwt.TokenProvider;
import com.example.hospitalproject.Entity.User.RoleUserGrade;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfigure {

    private final TokenProvider tokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPointHandler jwtAuthenticationEntryPointHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPointHandler)

                .and()
                .logout().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/home/signIn", "/home/signup", "/chat/create/service/center").permitAll()
                .antMatchers("/chat/create/private", "/chat/change/title/{id}", "/chat/delete/chat/{id}")
                .hasAnyAuthority("ROLE_ADMIN","ROLE_MANAGER", "ROLE_EXCELLENT_MEMBER", "ROLE_COMMON_MEMBER")
                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfig(tokenProvider));

        return httpSecurity.build();
    }
}
