package gov.cdc.dataingestion.report.integration.service.schema;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import gov.cdc.dataingestion.report.model.ValueSet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Loads value sets.
 */
@Service
public class ValueSetsService implements IValueSetsService {

    /**
     * Loads value sets.
     * @return Map of value sets.
     */
    public Map<String, ValueSet> execute(final String input) {
        String valueSetsDirectory = "./valuesets";
        return this.loadValueSetCatalog(valueSetsDirectory);
    }

    /**
     * loads value sets catalog.
     * @param catalog catalog folder name.
     * @return Map of value sets.
     */
    Map<String, ValueSet> loadValueSetCatalog(final String catalog) {
        List<ValueSet>  valueSets = new ArrayList<ValueSet>();
        try {
            var valueSetsDirectory = new ClassPathResource(catalog).getFile();
            if (!valueSetsDirectory.isDirectory()) {
                valueSets.addAll(this.readValueSets(valueSetsDirectory));
            } else if (Objects.requireNonNull(valueSetsDirectory.listFiles()).length >= 1) {
                Arrays.stream(Objects.requireNonNull(valueSetsDirectory.listFiles()))
                        .forEach(f -> valueSets.addAll(readValueSets(f)));
            }
            return valueSets.stream()
                    .collect(Collectors.toMap(ValueSet::getName, set -> set));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<ValueSet> readValueSets(final File file) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
        .configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, false)
                .configure(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS, false);
        mapper.findAndRegisterModules();
        try {
            return mapper.readerForListOf(ValueSet.class).readValue(
                    new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
