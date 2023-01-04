package com.vavito.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import jakarta.servlet.http.HttpServletRequest
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class JwtService(
    @Value("\${app.jwt.lifeTime}")
    val tokenLifeTime: Int,

    @Value("\${app.jwt.secret}")
    val secret: String,

    val userDetailsService: UserDetailsService
) {
    private val algorithm = Algorithm.HMAC256(secret)

    private val verifier = JWT.require(algorithm).build()

    fun extractToken(request: HttpServletRequest): String? {
        val header: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        return if (header != null && header.lowercase().startsWith("bearer ")) {
            header.substring(7)
        } else {
            header
        }
    }

    fun verify(token: String): UsernamePasswordAuthenticationToken {
        val decoded = verifier.verify(token)
        val user = userDetailsService.loadUserByUsername(decoded.subject)
        val authorities: MutableList<SimpleGrantedAuthority> = ArrayList()

        val rolesClaim = decoded.claims["roles"]
        if (rolesClaim != null && !rolesClaim.isMissing) {
            rolesClaim.asArray(String::class.java).forEach {
                authorities.add(SimpleGrantedAuthority(it))
            }
        }

        val authenticationToken = UsernamePasswordAuthenticationToken(
            user, null, authorities
        )
        SecurityContextHolder.getContext().authentication = authenticationToken
        return authenticationToken
    }

    fun generateToken(username: String, request: HttpServletRequest, roles: List<String>): String {
        return JWT.create()
            .withSubject(username)
            .withIssuedAt(DateTime().toDate())
            .withExpiresAt(DateTime().plusSeconds(tokenLifeTime).toDate())
            .withIssuer(request.requestURL.toString())
            .withClaim("roles", roles)
            .sign(algorithm)
    }
}