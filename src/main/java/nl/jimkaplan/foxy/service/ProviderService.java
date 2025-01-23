package nl.jimkaplan.foxy.service;

import lombok.RequiredArgsConstructor;
import nl.jimkaplan.foxy.model.Provider;
import nl.jimkaplan.foxy.repository.ProviderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderService {
    private final ProviderRepository repository;

    public Provider selectProvider(String organization, String project) {
        // Implementation with failover logic
        List<Provider> candidates = repository.findByUsageFlagTrueOrderByPriorityAsc();

        if (organization != null || project != null) {
            candidates = candidates.stream()
                    .filter(p -> matchesAssociations(p, organization, project))
                    .toList();
        }
        // Default to first available provider
        return candidates.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available providers"));
    }

    public boolean matchesAssociations(Provider provider, String org, String proj) {
        // Custom logic for organization/project matching
        if (org == null) {
            return provider.getProjects().contains(proj);
        } else if (proj == null) {
            return provider.getOrganizations().contains(org);
        }
        return provider.getOrganizations().contains(org) && provider.getProjects().contains(proj);
    }

    public Provider createProvider(Provider provider) {
        return repository.save(provider);
    }

    public Provider updateProvider(String id, Provider provider) {
        provider.setId(id); // Ensure the ID matches
        return repository.save(provider); // Uses built-in save() for update
    }

    public List<Provider> listProviders() {
        return repository.findAll();
    }
}
