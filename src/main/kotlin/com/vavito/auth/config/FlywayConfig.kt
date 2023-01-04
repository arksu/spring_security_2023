package com.vavito.auth.config

import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class FlywayConfig(
    dataSource: DataSource
) {
    init {
        Flyway
            .configure()
            .baselineOnMigrate(true)
            .dataSource(dataSource)
            .load()
            .migrate()
    }
}