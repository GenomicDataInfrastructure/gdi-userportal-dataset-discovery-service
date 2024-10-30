package io.github.genomicdatainfrastructure.discovery.filters.application.ports;

import java.util.List;

import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;

public interface FiltersRepository {

    List<ValueLabel> getValuesForFilter(String key);

}
