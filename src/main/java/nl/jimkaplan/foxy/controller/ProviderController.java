package nl.jimkaplan.foxy.controller;

import lombok.RequiredArgsConstructor;
import nl.jimkaplan.foxy.model.Provider;
import nl.jimkaplan.foxy.service.ProviderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {
    private final ProviderService service;

    @PostMapping
    public Provider createProvider(@RequestBody Provider provider) {
        return service.createProvider(provider);
    }

    @PutMapping("/{id}")
    public Provider updateProvider(@PathVariable String id, @RequestBody Provider provider) {
        return service.updateProvider(id, provider);
    }

    @GetMapping
    public List<Provider> listProviders() {
        return service.listProviders();
    }
}
