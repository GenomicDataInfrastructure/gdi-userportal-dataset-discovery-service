// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.mapper.CkanDatasetsMapper;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.mapper.CkanDatasetsMapperImpl;
import io.github.genomicdatainfrastructure.discovery.model.Agent;
import io.github.genomicdatainfrastructure.discovery.model.RetrievedDataset;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

/**
 * The CKAN deployment decides the shape of a dataset through its metadata profiles. These tests
 * read the same kind of dataset as the gdi_current profile serves it (one value per field) and as
 * the HealthDCAT-AP R8 profile serves it (lists, and the R8-only properties), through the real
 * Jackson configuration and the real mapper.
 */
class MetadataProfileShapesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CkanDatasetsMapper mapper = new CkanDatasetsMapperImpl();

    @BeforeEach
    void setUp() {
        new JacksonConfig().customize(objectMapper);
    }

    private RetrievedDataset retrieve(String json) throws JsonProcessingException {
        return mapper.map(objectMapper.readValue(json, CkanPackage.class));
    }

    @Nested
    class GdiCurrentShape {

        private static final String DATASET = """
                {
                  "id": "gdi-1",
                  "title": "Dataset",
                  "notes": "Description",
                  "url": "https://example.org/landing",
                  "dcat_type": {"name": "http://example.org/type/a", "display_name": "Type A"},
                  "publisher": [{
                    "name": "Publisher",
                    "email": "p@example.org",
                    "url": "https://publisher.example.org"
                  }],
                  "resources": [{
                    "id": "r1",
                    "name": "Distribution",
                    "description": "Distribution description",
                    "access_url": "https://example.org/access",
                    "download_url": "https://example.org/download",
                    "rights": "Open access",
                    "hash": "abc123",
                    "hash_algorithm": {"name": "http://example.org/sha256", "display_name": "SHA-256"}
                  }]
                }
                """;

        @Test
        void single_values_are_read_as_one_entry_lists() throws JsonProcessingException {
            var dataset = retrieve(DATASET);

            assertThat(dataset.getUrl()).containsExactly("https://example.org/landing");
            assertThat(dataset.getDcatType()).extracting(ValueLabel::getLabel)
                    .containsExactly("Type A");

            Agent publisher = dataset.getPublishers().getFirst();
            assertThat(publisher.getEmail()).containsExactly("p@example.org");
            assertThat(publisher.getUrl()).containsExactly("https://publisher.example.org");

            var distribution = dataset.getDistributions().getFirst();
            assertThat(distribution.getAccessUrl()).containsExactly("https://example.org/access");
            assertThat(distribution.getDownloadUrl()).containsExactly(
                    "https://example.org/download");
            assertThat(distribution.getRights()).extracting(ValueLabel::getValue,
                    ValueLabel::getLabel)
                    .containsExactly(tuple("Open access", "Open access"));
        }

        @Test
        void the_plain_hash_is_the_checksum() throws JsonProcessingException {
            var distribution = retrieve(DATASET).getDistributions().getFirst();

            assertThat(distribution.getChecksum()).isEqualTo("abc123");
            assertThat(distribution.getChecksumAlgorithm().getLabel()).isEqualTo("SHA-256");
        }

        @Test
        void the_properties_only_r8_defines_are_empty() throws JsonProcessingException {
            var dataset = retrieve(DATASET);

            assertThat(dataset.getCustodian()).isEmpty();
            assertThat(dataset.getHdabCoordinator()).isEmpty();
            assertThat(dataset.getVariables()).isEmpty();
            assertThat(dataset.getHasStructuredData()).isNull();
            assertThat(dataset.getDistributions().getFirst().getHasPolicy()).isNull();
        }
    }

    @Nested
    class HealthDcatApR8Shape {

        private static final String DATASET = """
                {
                  "id": "r8-1",
                  "title": "Dataset",
                  "notes": "Description",
                  "url": ["https://example.org/a", "https://example.org/b"],
                  "dcat_type": [
                    {"name": "http://example.org/type/a", "display_name": "Type A"},
                    {"name": "http://example.org/type/b", "display_name": "Type B"}
                  ],
                  "alternative_title": ["Alternative 1", "Alternative 2"],
                  "has_structured_data": true,
                  "related_resource": ["https://example.org/related"],
                  "source": ["https://example.org/source"],
                  "publisher": [{
                    "name": "Publisher",
                    "email": ["a@example.org", "b@example.org"],
                    "url": ["https://publisher.example.org/1", "https://publisher.example.org/2"],
                    "telephone": ["+31 20 1234567"],
                    "opening_hours": [{"description": "Weekdays", "frequency": ["Mo-Fr"]}],
                    "special_opening_hours": []
                  }],
                  "custodian": [{
                    "uri": "https://example.org/custodian",
                    "name": "Custodian",
                    "type": {"name": "http://example.org/agent-type/a", "display_name": "Agent type"},
                    "email": ["c@example.org"]
                  }],
                  "hdab_coordinator": [{"name": "Coordinator", "url": ["https://example.org/coordinator"]}],
                  "variables": [{
                    "title": "Group",
                    "title_translated": {"en": "Group", "nl": "Groep"},
                    "tables": [{
                      "title_translated": "Typed without a language",
                      "keyword": ["k1"],
                      "url": "https://example.org/table.csv",
                      "columns": [{
                        "name": "age",
                        "title": "Age",
                        "description_translated": {"nl": "Leeftijd"},
                        "datatype": "integer",
                        "property_url": "https://example.org/age"
                      }]
                    }]
                  }],
                  "qualified_relation": [{
                    "relation": ["http://example.org/rel/1", "http://example.org/rel/2"],
                    "role": [{"name": "http://example.org/role/a", "display_name": "Role A"}]
                  }],
                  "qualified_attribution": [{
                    "role": [{"name": "http://example.org/role/b", "display_name": "Role B"}],
                    "agent": [{"name": "Attributed"}]
                  }],
                  "resources": [{
                    "id": "r2",
                    "name": "Distribution",
                    "description": "Distribution description",
                    "access_url": ["https://example.org/access/1", "https://example.org/access/2"],
                    "download_url": ["https://example.org/download"],
                    "rights": [
                      {"uri": "https://example.org/licence", "label": "Licence"},
                      {"label": "Only a label"},
                      {"uri": "https://example.org/only-an-iri"}
                    ],
                    "checksum": [{
                      "algorithm": {"name": "http://example.org/sha256", "display_name": "SHA-256"},
                      "checksum_value": "abc123"
                    }],
                    "has_policy": "https://example.org/policy"
                  }]
                }
                """;

        @Test
        void lists_keep_every_value() throws JsonProcessingException {
            var dataset = retrieve(DATASET);

            assertThat(dataset.getUrl()).containsExactly("https://example.org/a",
                    "https://example.org/b");
            assertThat(dataset.getDcatType()).extracting(ValueLabel::getLabel)
                    .containsExactly("Type A", "Type B");

            Agent publisher = dataset.getPublishers().getFirst();
            assertThat(publisher.getEmail()).containsExactly("a@example.org", "b@example.org");
            assertThat(publisher.getUrl()).containsExactly("https://publisher.example.org/1",
                    "https://publisher.example.org/2");
            assertThat(publisher.getTelephone()).containsExactly("+31 20 1234567");
            assertThat(publisher.getOpeningHours()).hasSize(1);
            assertThat(publisher.getOpeningHours().getFirst().getFrequency())
                    .containsExactly("Mo-Fr");
            assertThat(publisher.getSpecialOpeningHours()).isEmpty();
        }

        @Test
        void the_r8_only_properties_are_mapped() throws JsonProcessingException {
            var dataset = retrieve(DATASET);

            assertThat(dataset.getAlternativeTitle()).containsExactly("Alternative 1",
                    "Alternative 2");
            assertThat(dataset.getHasStructuredData()).isTrue();
            assertThat(dataset.getRelatedResource()).containsExactly("https://example.org/related");
            assertThat(dataset.getSource()).containsExactly("https://example.org/source");

            var custodian = dataset.getCustodian().getFirst();
            assertThat(custodian.getUri()).isEqualTo("https://example.org/custodian");
            assertThat(custodian.getName()).isEqualTo("Custodian");
            assertThat(custodian.getType().getLabel()).isEqualTo("Agent type");
            assertThat(custodian.getEmail()).containsExactly("c@example.org");

            assertThat(dataset.getHdabCoordinator().getFirst().getUrl())
                    .containsExactly("https://example.org/coordinator");
        }

        @Test
        void variables_keep_their_tables_and_columns() throws JsonProcessingException {
            var group = retrieve(DATASET).getVariables().getFirst();

            // CKAN resolved title is used, the text per language only when it is missing
            assertThat(group.getTitle()).isEqualTo("Group");

            var table = group.getTables().getFirst();
            assertThat(table.getTitle()).isEqualTo("Typed without a language");
            assertThat(table.getKeyword()).containsExactly("k1");
            assertThat(table.getUrl()).isEqualTo("https://example.org/table.csv");

            var column = table.getColumns().getFirst();
            assertThat(column.getName()).isEqualTo("age");
            assertThat(column.getTitle()).isEqualTo("Age");
            assertThat(column.getDescription()).isEqualTo("Leeftijd");
            assertThat(column.getDatatype()).isEqualTo("integer");
            assertThat(column.getPropertyUrl()).isEqualTo("https://example.org/age");
        }

        @Test
        void relations_and_attributions_keep_every_relation_and_role() throws JsonProcessingException {
            var dataset = retrieve(DATASET);

            var relation = dataset.getQualifiedRelation().getFirst();
            assertThat(relation.getRelation()).containsExactly("http://example.org/rel/1",
                    "http://example.org/rel/2");
            assertThat(relation.getRole()).extracting(ValueLabel::getLabel)
                    .containsExactly("Role A");

            var attribution = dataset.getQualifiedAttribution().getFirst();
            assertThat(attribution.getRole()).extracting(ValueLabel::getLabel)
                    .containsExactly("Role B");
        }

        @Test
        void a_distribution_keeps_its_lists_rights_and_checksum_entity() throws JsonProcessingException {
            var distribution = retrieve(DATASET).getDistributions().getFirst();

            assertThat(distribution.getAccessUrl()).containsExactly("https://example.org/access/1",
                    "https://example.org/access/2");
            assertThat(distribution.getDownloadUrl()).containsExactly(
                    "https://example.org/download");
            assertThat(distribution.getRights()).extracting(ValueLabel::getValue,
                    ValueLabel::getLabel)
                    .containsExactly(
                            tuple("https://example.org/licence", "Licence"),
                            tuple("Only a label", "Only a label"),
                            tuple("https://example.org/only-an-iri",
                                    "https://example.org/only-an-iri"));
            assertThat(distribution.getChecksum()).isEqualTo("abc123");
            assertThat(distribution.getChecksumAlgorithm().getLabel()).isEqualTo("SHA-256");
            assertThat(distribution.getHasPolicy()).isEqualTo("https://example.org/policy");
        }
    }

    @Nested
    class FutureProfiles {

        @Test
        void properties_the_model_does_not_know_are_ignored() throws JsonProcessingException {
            var dataset = retrieve("""
                    {
                      "id": "x",
                      "title": "Dataset",
                      "notes": "Description",
                      "some_future_field": {"nested": [1, 2, {"deeper": true}]},
                      "publisher": [{"name": "Publisher", "some_future_agent_field": ["a"]}],
                      "resources": [{"id": "r", "name": "Distribution", "description": "D",
                        "some_future_resource_field": "value"}]
                    }
                    """);

            assertThat(dataset.getTitle()).isEqualTo("Dataset");
            assertThat(dataset.getPublishers().getFirst().getName()).isEqualTo("Publisher");
            assertThat(dataset.getDistributions()).hasSize(1);
        }

        @Test
        void a_text_served_as_a_list_uses_its_first_text() throws JsonProcessingException {
            assertThat(retrieve("""
                    {"id": "x", "title": "T", "notes": "N", "version": ["", "2.0", "3.0"]}
                    """).getVersion()).isEqualTo("2.0");
        }

        @Test
        void a_text_served_per_language_uses_english_first() throws JsonProcessingException {
            assertThat(retrieve("""
                    {"id": "x", "title": "T", "notes": "N", "version": {"nl": "Een", "en": "One"}}
                    """).getVersion()).isEqualTo("One");
        }

        @Test
        void a_text_served_per_language_without_english_uses_the_first_language() throws JsonProcessingException {
            assertThat(retrieve("""
                    {"id": "x", "title": "T", "notes": "N", "version": {"nl": "Een"}}
                    """).getVersion()).isEqualTo("Een");
        }

        @Test
        void a_nested_text_served_as_a_list_uses_its_first_text() throws JsonProcessingException {
            var dataset = retrieve("""
                    {"id": "x", "title": "T", "notes": "N",
                     "resources": [{"id": "r", "name": "D", "description": ["First", "Second"]}]}
                    """);

            assertThat(dataset.getDistributions().getFirst().getDescription()).isEqualTo("First");
        }

        @Test
        void a_value_label_served_as_a_list_uses_its_first_usable_entry() throws JsonProcessingException {
            var dataset = retrieve("""
                    {"id": "x", "title": "T", "notes": "N",
                     "access_rights": [
                       "",
                       {"name": "http://example.org/open", "display_name": "Open"},
                       {"name": "http://example.org/closed", "display_name": "Closed"}
                     ]}
                    """);

            assertThat(dataset.getAccessRights().getLabel()).isEqualTo("Open");
        }

        @Test
        void a_rights_list_leaves_out_blank_statements() throws JsonProcessingException {
            var dataset = retrieve("""
                    {"id": "x", "title": "T", "notes": "N",
                     "resources": [{"id": "r", "name": "D", "description": "D",
                       "rights": ["", {"label": " "}, "Real"]}]}
                    """);

            assertThat(dataset.getDistributions().getFirst().getRights())
                    .extracting(ValueLabel::getValue)
                    .containsExactly("Real");
        }

        @Test
        void a_checksum_algorithm_served_as_a_plain_iri_keeps_the_iri() throws JsonProcessingException {
            var dataset = retrieve(
                    """
                            {"id": "x", "title": "T", "notes": "N",
                             "resources": [{"id": "r", "name": "D", "description": "D",
                               "checksum": [{"algorithm": "http://spdx.org/rdf/terms#checksumAlgorithm_sha256",
                                             "checksum_value": "abc123"}]}]}
                            """);

            var distribution = dataset.getDistributions().getFirst();
            assertThat(distribution.getChecksum()).isEqualTo("abc123");
            assertThat(distribution.getChecksumAlgorithm().getValue()).isEqualTo(
                    "http://spdx.org/rdf/terms#checksumAlgorithm_sha256");
        }

        @Test
        void a_mismatch_that_is_not_a_text_still_fails() {
            assertThatThrownBy(() -> retrieve("""
                    {"id": "x", "title": "T", "notes": "N", "number_of_records": {"a": 1}}
                    """)).isInstanceOf(MismatchedInputException.class);
        }

        @Test
        void the_search_result_shows_the_first_usable_dataset_type() throws JsonProcessingException {
            var ckanPackage = objectMapper.readValue("""
                    {"id": "x", "title": "T",
                     "dcat_type": [
                       {"name": " ", "display_name": " "},
                       {"name": "http://example.org/type/b", "display_name": "Type B"}
                     ]}
                    """, CkanPackage.class);

            assertThat(mapper.mapToSearchedDataset(ckanPackage).getDatasetType())
                    .isEqualTo("Type B");
        }
    }
}
