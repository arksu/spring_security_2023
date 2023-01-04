package com.vavito.auth.security

import com.auth0.jwt.exceptions.JWTVerificationException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthorizationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtService.extractToken(request)
        if (token != null && SecurityContextHolder.getContext().authentication == null) {
            try {
                val authentication = jwtService.verify(token)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication

                filterChain.doFilter(request, response)
            } catch (e: JWTVerificationException) {
                SecurityContextHolder.clearContext()
                response.sendError(HttpStatus.FORBIDDEN.value(), e.message)
            } catch (e: UsernameNotFoundException) {
                SecurityContextHolder.clearContext()
                response.sendError(HttpStatus.BAD_REQUEST.value(), e.message)
            }
        } else {
            filterChain.doFilter(request, response)
        }
    }
}