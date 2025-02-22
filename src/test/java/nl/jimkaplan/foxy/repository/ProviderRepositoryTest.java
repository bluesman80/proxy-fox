package nl.jimkaplan.foxy.repository;

import nl.jimkaplan.foxy.model.Provider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ProviderRepositoryTest {

    @Autowired
    private ProviderRepository providerRepository;

    private Provider provider1;
    private Provider provider2;

    @BeforeEach
    void setUp() {
        providerRepository.deleteAll();
        // Create test data
        provider1 = new Provider();
        provider1.setName("Provider1");
        provider1.setUsageFlag(true);
        provider1.setPriority(1);
        provider1.setOrganizations(List.of("Org1"));
        provider1.setProjects(List.of("Project1"));
        provider1.setDefaultModel("gpt-3.5-turbo");

        provider2 = new Provider();
        provider2.setName("Provider2");
        provider2.setUsageFlag(true);
        provider2.setPriority(2);
        provider2.setOrganizations(List.of("Org2"));
        provider2.setProjects(List.of("Project2"));
        provider2.setDefaultModel("gpt-44");
        // Save test data to the database
        providerRepository.save(provider1);
        providerRepository.save(provider2);
    }

    @AfterEach
    void tearDown() {
        // Clean up the test database after each test
        providerRepository.deleteAll();
    }

    @Test
    void testFindByName() {
        // Act
        Provider result = providerRepository.findByName("Provider1");

        // Assert
        assertNotNull(result);
        assertEquals("Provider1", result.getName());
    }

    @Test
    void testFindByUsageFlagTrueOrderByPriorityAsc() {
        // Act
        List<Provider> result = providerRepository.findByUsageFlagTrueOrderByPriorityAsc();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Provider1", result.get(0).getName()); // Lower priority first
        assertEquals("Provider2", result.get(1).getName());
    }

    @Test
    void testSaveAndFindAll() {
        // Act
        List<Provider> result = providerRepository.findAll();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(provider1));
        assertTrue(result.contains(provider2));
    }
}