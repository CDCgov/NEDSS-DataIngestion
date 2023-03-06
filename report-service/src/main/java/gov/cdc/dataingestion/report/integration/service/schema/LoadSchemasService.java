    package gov.cdc.dataingestion.report.integration.service.schema;

    import com.fasterxml.jackson.databind.MapperFeature;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
    import gov.cdc.dataingestion.report.model.Element;
    import gov.cdc.dataingestion.report.model.Schema;
    import gov.cdc.dataingestion.report.model.Value;
    import gov.cdc.dataingestion.report.model.ValueSet;
    import lombok.extern.slf4j.Slf4j;
    import org.jetbrains.annotations.NotNull;
    import org.jetbrains.annotations.Nullable;
    import org.springframework.core.io.ClassPathResource;
    import org.springframework.stereotype.Service;
    import java.io.File;
    import java.io.FileInputStream;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;
    import java.util.Objects;
    import java.util.stream.Collectors;
    import java.util.stream.Stream;

    /**
     * Load schemas service.
     */
    @Service
    @Slf4j
    public class LoadSchemasService implements ILoadSchemasService {

        /**
         * Value Sets Service
         */
        private final ValueSetsService valueSetsService;

        /**
         * Value set service
         * @param valueSetsService Value set service.
         */
        public LoadSchemasService(
                @NotNull final ValueSetsService valueSetsService) {
            this.valueSetsService = valueSetsService;
        }

        /**
         * load schemas and build schema instances.
         * @return Map of schema instances.
         */
        public Map<String, Schema> execute(@NotNull final String input) {
            String schemasDirectory = "./schemas";
            return this.loadSchemas(schemasDirectory);
        }

        /**
         * loads value sets catalog.
         * @param catalog catalog folder name.
         * @return Map of value sets.
         */
       private Map<String, Schema> loadSchemas(@NotNull final String catalog) {
            Map<String, Schema> schemas;

                // Build schema instances.
                schemas = this.buildSchemas(catalog);

                // Load value sets.
               var valueSets = this.valueSetsService.execute("");

                // Create Base schemas.
                var fixedSchemas  = schemas.entrySet().stream()
                        .filter(s ->  this.hasNoExtendedSchema(s.getValue()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                s -> this.fixBaseSchema(s.getValue(), valueSets)));

                // Fix extended or based on schemas.
               var inheritedSchemas  = schemas.entrySet().stream()
                        .filter(s ->  !this.hasNoExtendedSchema(s.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


                while (inheritedSchemas.size() > 0) {

                    // Fix schemas with parent schemas converted
                   var fixedInheritedSchemas =
                    inheritedSchemas.entrySet().stream()
                            .filter(s ->   fixedSchemas.get(this.getParentSchemaName(s.getValue())) != null)
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    s -> this.fixInheritedSchema(
                                            s.getValue(),
                                            fixedSchemas.get(
                                                    this.getParentSchemaName(
                                                            s.getValue())),
                                            valueSets)));
                   fixedSchemas.putAll(fixedInheritedSchemas);

                   // remove schemas from list if already converted.
                   inheritedSchemas.entrySet()
                           .removeIf(s -> fixedSchemas.get(s.getKey()) != null);
                }
                return fixedSchemas;
        }

        private boolean hasNoExtendedSchema(@NotNull final Schema schema) {
            return schema.getBasedOn() == null
                    && schema.getExtendsSchema() == null;
        }

        @NotNull
        private String getParentSchemaName(@NotNull final Schema schema) {
            var parentSchema = schema.getBasedOn() == null
                    ? schema.getExtendsSchema()
                    : schema.getBasedOn();
            return  parentSchema.lastIndexOf("/") == -1
                    ? parentSchema
                    : parentSchema.substring(parentSchema.lastIndexOf("/") + 1);
        }

        @NotNull
        private List<String> getListOfFiles(@NotNull final String directory) {
            try {
                Path path = Path.of(new ClassPathResource(directory)
                        .getFile().toURI());
                try (Stream<Path> stream = Files.walk(Path.of(path.toUri()))) {
                    return stream.map(String::valueOf).sorted().collect(Collectors.toList());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @NotNull
        private Map<String, Schema> buildSchemas(@NotNull final String catalog)  {
            var listOfFiles =  this.getListOfFiles(catalog)
                    .stream().filter(n -> n.endsWith(".schema"))
                    .map(File::new).toList();

            return listOfFiles.stream()
                    .map(this::readSchema)
                    .collect(Collectors.toMap(Schema::getName, schema -> schema, (a, b) -> b));
        }

        @NotNull
        private Schema fixBaseSchema(
                @NotNull final Schema schema,
                @NotNull final Map<String, ValueSet> valueSets) {
            ArrayList<Element> elements = new ArrayList<>();
            for (Element e : schema.getElements()) {
                elements.add(this.fixupElement(e, valueSets));
            }
            schema.setElements(elements);

            return schema;
        }

        @NotNull
        private Schema fixInheritedSchema(
                @NotNull final Schema schema,
                @NotNull final Schema baseSchema,
                @NotNull final Map<String, ValueSet> valueSets) {
            ArrayList<Element> elements = new ArrayList<>();
            Map<String, Element> baseSchemaElements =
                    baseSchema.getElements().stream().collect(Collectors.toMap(Element::getName, element -> element));
            for (Element e : schema.getElements()) {
                var baseElement  = baseSchemaElements.get(e.getName());
                if (baseElement != null) {
                    elements.add(this.fixupElementWithBaseElement(e, baseSchemaElements.get(e.getName()), valueSets));
                } else {
                    elements.add(this.fixupElement(e, valueSets));
                }
               }
            schema.setElements(elements);

            return schema;
        }

        /**
         *
         * @param file file To read
         * @return Schema instance.
         */
        @NotNull
        private Schema readSchema(@NotNull final File file) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
                    .configure(
                            MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS,
                            false)
                    .configure(
                            MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS,
                            false);
            mapper.findAndRegisterModules();
            try {
                return mapper.readValue(
                        new FileInputStream(file), Schema.class);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }


        /**
         * The fixup process fills in references and inherited attributes.
         */
        @NotNull
        private Element fixupElement(@NotNull final Element element, @NotNull final Map<String, ValueSet> valueSets) {

            var valueSet = element.getValueSet();
            var valueSetRef = this.buildValueSetReference(valueSets.get(valueSet), element.getAltValues());
            element.setValueSetRef(valueSetRef);
            return element;
        }

        /**
         * Add references for the element.
         * @param element  Element.
         * @param baseElement Bse elewmnt
         * @param valueSets Value sets.
         * @return Enhanced element with reference values.
         */
        @NotNull
        private Element fixupElementWithBaseElement(
                @NotNull final Element element,
                @NotNull final Element baseElement,
                @NotNull final Map<String, ValueSet> valueSets) {

            var valueSet =
                    element.getValueSet() != null
                            ? element.getValueSet() : baseElement.getValueSet();

            var  valueSetRef = this.buildValueSetReference(valueSets.get(valueSet), element.getAltValues());

            Element fullElement = ElementExtensions.mergeWithBase(element, baseElement);
            fullElement.setValueSetRef(valueSetRef);
            return fullElement;
        }


        /**
         *
         * @param valueSet Value set
         * @param altValues alternate values
         * @return Value set with merged alternate values.
         */
        @NotNull
        private ValueSet buildValueSetReference(
                @Nullable final ValueSet valueSet,
                @Nullable final List<Value> altValues) {
            ArrayList<Value> mergedValues = new ArrayList<Value>();
            // if we have alt values then we need to merge them in
            if (valueSet != null  && (altValues != null && !altValues.isEmpty())) {
                var altValuesCodes = altValues.stream().map(Value::getCode).toList();
                mergedValues.addAll(Objects.requireNonNull(valueSet).getValues().stream()
                        .filter(value -> !altValuesCodes.contains(value.getCode())).toList());
                mergedValues.addAll(altValues);
                valueSet.setValues(mergedValues);
                return valueSet;

            }
            return valueSet;
        }
    }
