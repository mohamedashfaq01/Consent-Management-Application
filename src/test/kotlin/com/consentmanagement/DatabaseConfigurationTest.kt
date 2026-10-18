package com.consentmanagement

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.sql.DataSource

@SpringBootTest
class DatabaseConfigurationTest {

    @Autowired
    private lateinit var dataSource: DataSource

    @Test
    fun `application connects to H2 and Flyway has initialized the schema`() {
        dataSource.connection.use { connection ->
            assertTrue(connection.isValid(2))
            val url = connection.metaData.url
            assertTrue(url.contains("h2", ignoreCase = true), "expected H2 JDBC URL but was $url")

            connection.createStatement().use { statement ->
                statement.executeQuery("""SELECT COUNT(*) FROM "flyway_schema_history"""").use { resultSet ->
                    assertTrue(resultSet.next())
                    assertTrue(resultSet.getInt(1) >= 1)
                }
            }
        }
    }
}
