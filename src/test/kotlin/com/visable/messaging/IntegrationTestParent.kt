package com.visable.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

/**
 * Base class for all Spring Boot integration tests.
 */
@ActiveProfiles("integration-test")
@ExtendWith(SpringExtension::class)
@AutoConfigureRestDocs
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTestParent {
    @LocalServerPort
    var localPort: Int = 0

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setup() {
        setupRestAssured()
        resetDatabase()
    }

    private fun setupRestAssured() {
        RestAssured.port = localPort
    }

    private fun resetDatabase() {
        ALL_TABLES.forEach { table -> jdbcTemplate.update("DELETE FROM \"$table\"") }
    }

    companion object {
        private val ALL_TABLES =
            listOf(
                "message",
                "user",
            )
    }
}
