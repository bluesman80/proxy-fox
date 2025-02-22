package nl.jimkaplan.foxy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Providers", description = "Provider management endpoints")
public class ProviderController {
    private final ProviderService service;

    @Operation(summary = "Create provider", description = "Creates a new AI provider")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid provider data")
    })
    @PostMapping
    public Provider createProvider(@RequestBody Provider provider) {
        return service.createProvider(provider);
    }

    @Operation(summary = "Update provider", description = "Updates an existing provider by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider updated successfully"),
        @ApiResponse(responseCode = "404", description = "Provider not found"),
        @ApiResponse(responseCode = "400", description = "Invalid provider data")
    })
    @PutMapping("/{id}")
    public Provider updateProvider(@PathVariable String id, @RequestBody Provider provider) {
        return service.updateProvider(id, provider);
    }

    @Operation(summary = "List providers", description = "Returns all available providers")
    @ApiResponse(responseCode = "200", description = "List of providers retrieved successfully")
    @GetMapping
    public List<Provider> listProviders() {
        return service.listProviders();
    }
}
