package com.vavito.auth.controller

import com.vavito.auth.entity.User
import com.vavito.auth.repo.UserRepo
import com.vavito.auth.security.JwtService
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${app.api-base-path}/auth")
class AuthenticationController(
    val userRepo: UserRepo,
    val passwordEncoder: PasswordEncoder,
    val jwtService: JwtService,
    val authenticationManager: AuthenticationManager
) {
    @PostMapping("register")
    @SecurityRequirements
    fun createUser(
        @RequestBody dto: RegistrationRequest,
        request: HttpServletRequest
    ): AuthenticationResponse {
        val newUser = User(
            login = dto.login, passwordHash = passwordEncoder.encode(dto.password),
        )
        userRepo.save(newUser)

        return AuthenticationResponse(
            jwtService.generateToken(
                newUser.login, request, listOf()
            )
        )
    }

    @PostMapping("login")
    @SecurityRequirements
    fun login(
        @RequestBody
        dto: LoginRequest,
        request: HttpServletRequest
    ): AuthenticationResponse {
        val authenticationToken = UsernamePasswordAuthenticationToken(
            dto.login, dto.password
        )
        val authentication = authenticationManager.authenticate(authenticationToken)
        val user: User = authentication.principal as User
        val roles = authentication.authorities.map {
            it.authority
        }.toList()

        return AuthenticationResponse(
            jwtService.generateToken(
                user.username, request, roles
            )
        )
    }
}

data class RegistrationRequest(
    val login: String,
    val password: String
)

data class LoginRequest(
    val login: String,
    val password: String
)

data class AuthenticationResponse(
    val token: String
)