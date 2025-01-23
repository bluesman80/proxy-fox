package nl.jimkaplan.foxy.repository;

import nl.jimkaplan.foxy.model.Provider;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProviderRepository extends MongoRepository<Provider, String> {
    Provider findByName(String name);

    List<Provider> findByUsageFlagTrueOrderByPriorityAsc();
}
