package nl.jimkaplan.foxy.service;

import nl.jimkaplan.foxy.model.Provider;
import nl.jimkaplan.foxy.repository.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ProviderServiceTest {

    @Autowired
    private ProviderService providerService;

    @MockitoBean
    private ProviderRepository repository;

    private Provider provider1;
    private Provider provider2;

    @BeforeEach
    void setUp() {
        provider1 = new Provider();
        provider1.setId("1");
        provider1.setName("Provider1");
        provider1.setUsageFlag(true);
        provider1.setPriority(1);
        provider1.setOrganizations(List.of("Org1"));
        provider1.setProjects(List.of("Project1"));

        provider2 = new Provider();
        provider2.setId("2");
        provider2.setName("Provider2");
        provider2.setUsageFlag(true);
        provider2.setPriority(2);
        provider2.setOrganizations(List.of("Org2"));
        provider2.setProjects(List.of("Project2"));
    }

    @Test
    void testSelectProvider_NoFilters() {
        // Arrange
        given(repository.findByUsageFlagTrueOrderByPriorityAsc())
                .willReturn(List.of(provider1, provider2));

        // Act
        Provider result = providerService.selectProvider(null, null);

        // Assert
        assertEquals(provider1, result);
    }

    @Test
    void testSelectProvider_WithOrganizationFilter() {
        // Arrange
        given(repository.findByUsageFlagTrueOrderByPriorityAsc())
                .willReturn(List.of(provider1, provider2));

        // Act
        Provider result = providerService.selectProvider("Org1", null);

        // Assert
        assertEquals(provider1, result);
    }

    @Test
    void testSelectProvider_WithProjectFilter() {
        // Arrange
        given(repository.findByUsageFlagTrueOrderByPriorityAsc())
                .willReturn(List.of(provider1, provider2));

        // Act
        Provider result = providerService.selectProvider(null, "Project2");

        // Assert
        assertEquals(provider2, result);
    }

    @Test
    void testSelectProvider_NoAvailableProviders() {
        // Arrange
        given(repository.findByUsageFlagTrueOrderByPriorityAsc())
                .willReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                providerService.selectProvider(null, null));
        assertEquals("No available providers", exception.getMessage());
    }

    @Test
    void testMatchesAssociations_OrgOnly() {
        // Act
        boolean result = providerService.matchesAssociations(provider1, "Org1", null);

        // Assert
        assertTrue(result);
    }

    @Test
    void testMatchesAssociations_ProjectOnly() {
        // Act
        boolean result = providerService.matchesAssociations(provider2, null, "Project2");

        // Assert
        assertTrue(result);
    }

    @Test
    void testMatchesAssociations_BothOrgAndProject() {
        // Act
        boolean result = providerService.matchesAssociations(provider1, "Org1", "Project1");

        // Assert
        assertTrue(result);
    }

    @Test
    void testMatchesAssociations_NoMatch() {
        // Act
        boolean result = providerService.matchesAssociations(provider1, "Org2", "Project2");

        // Assert
        assertFalse(result);
    }

    @Test
    void testCreateProvider() {
        // Arrange
        given(repository.save(provider1)).willReturn(provider1);

        // Act
        Provider result = providerService.createProvider(provider1);

        // Assert
        assertEquals(provider1, result);
    }

    @Test
    void testUpdateProvider() {
        // Arrange
        Provider updatedProvider = copyFrom(provider1);
        updatedProvider.setName("UpdatedProvider");
        updatedProvider.setPriority(99);

        given(repository.save(any(Provider.class))).willReturn(updatedProvider);

        // Act
        Provider result = providerService.updateProvider("1", updatedProvider);

        // Assert
        assertEquals(updatedProvider, result);
        assertEquals("1", result.getId());
        assertEquals("UpdatedProvider", result.getName());
        assertEquals(99, result.getPriority());
    }

    @Test
    void testListProviders() {
        // Arrange
        given(repository.findAll()).willReturn(List.of(provider1, provider2));

        // Act
        List<Provider> result = providerService.listProviders();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(provider1));
        assertTrue(result.contains(provider2));
    }

    private Provider copyFrom(Provider provider) {
        Provider clone = new Provider();
        clone.setId(provider.getId());
        clone.setName(provider.getName());
        clone.setUrl(provider.getUrl());
        clone.setApiKey(provider.getApiKey());
        clone.setUsageFlag(provider.isUsageFlag());
        clone.setPriority(provider.getPriority());
        clone.setOrganizations(provider.getOrganizations());
        clone.setProjects(provider.getProjects());
        return clone;
    }
}