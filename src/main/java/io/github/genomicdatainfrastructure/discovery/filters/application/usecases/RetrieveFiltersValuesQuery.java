package io.github.genomicdatainfrastructure.discovery.filters.application.usecases;

import java.util.List;

import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FiltersRepository;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import jakarta.inject.Inject;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RetrieveFiltersValuesQuery {

    private final FiltersRepository filtersRepository;

    public List<ValueLabel> execute(String key) {
        return filtersRepository.getValuesForFilter(key);
    }

}
