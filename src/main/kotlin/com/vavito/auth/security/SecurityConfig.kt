package com.vavito.auth.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
class SecurityConfig(
    @Value("\${app.api-base-path}")
    val apiBasePath: String,

    val jwtAuthorizationFilter: JwtAuthorizationFilter,
    val authenticationProvider: AuthenticationProvider
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.authorizeHttpRequests { authorize ->
            // swagger endpoints
            authorize.requestMatchers("/swagger-ui/**").permitAll()
            authorize.requestMatchers("/v3/api-docs/**").permitAll()

            // actuator
            authorize.requestMatchers("/actuator/**").permitAll()

            // always available endpoints
            authorize.requestMatchers("$apiBasePath/auth/login").permitAll()
            authorize.requestMatchers("$apiBasePath/auth/register").permitAll()

            authorize.anyRequest().authenticated()
        }

        http.authenticationProvider(authenticationProvider)
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)

        http.formLogin().disable()
        http.logout().disable()
        http.cors()

        return http.build()
    }
}