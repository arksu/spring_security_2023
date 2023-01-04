package com.vavito.auth.controller

import com.vavito.auth.security.IsAdmin
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${app.api-base-path}")
class Test {
    @GetMapping("test/admin")
    @IsAdmin
    fun testAdmin(): String {
        return "you is admin"
    }

    @GetMapping("test")
    fun testGet(): String {
        return "ok"
    }
}