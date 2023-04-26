package org.conservationco.asanahire

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
internal class AsanaHireApplicationTest {

    /**
     * Integration test that verifies if the Spring Boot application context loads successfully.
     *
     * Functionally, this verifies all the following:
     *   * All configuration is set properly
     *   * All beans are autowired / dependency injection works properly
     *   * The application context loads successfully
     */
    @Test
    fun `application loads successfully`() { }

}
