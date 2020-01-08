/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaweb.vehiclerouting.service.region;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.neovisionaries.i18n.CountryCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.configurationprocessor.metadata.ConfigurationMetadata;
import org.springframework.boot.configurationprocessor.metadata.ItemHint;
import org.springframework.boot.configurationprocessor.metadata.ItemMetadata;

import static org.assertj.core.api.Assertions.assertThat;

class CountryCodeMetadataGeneratorTest {

    private static final String COUNTRY_CODES_PROPERTY_NAME = "app.region.country-codes";

    @Test
    void metadata_should_contain_all_country_codes() throws NoSuchFieldException, NoSuchMethodException {
        ConfigurationMetadata metadata = CountryCodeMetadataGenerator.metadata();

        assertThat(metadata.getItems()).isNotEmpty();
        ItemMetadata property = metadata.getItems().get(0);
        assertThat(property.getName()).isEqualTo(COUNTRY_CODES_PROPERTY_NAME);
        assertThat(property.getSourceType()).isEqualTo(RegionProperties.class.getName());

        assertThat(metadata.getHints()).isNotEmpty();
        ItemHint hint = metadata.getHints().get(0);
        assertThat(hint.getName()).isEqualTo(COUNTRY_CODES_PROPERTY_NAME);
        assertThat(hint.getValues()).hasSameSizeAs(
                Arrays.stream(CountryCode.values())
                        .filter(countryCode -> countryCode != CountryCode.UNDEFINED)
                        .collect(Collectors.toList()));
        assertThat(hint.getProviders()).isEmpty();
    }

    @Test
    void module_root_should_be_ancestor_of_this_file() {
        File moduleRoot = CountryCodeMetadataGenerator.moduleRoot();
        assertThat(moduleRoot).isDirectoryContaining(file -> file.getName().equals("target"));
    }

    @Test
    void metadata_file_should_be_created_under_root(@TempDir File root) throws IOException {
        File metadataFile = CountryCodeMetadataGenerator.prepareMetadataFile(root);
        assertThat(metadataFile.getAbsolutePath())
                .startsWith(root.getAbsolutePath())
                .endsWith("src/main/resources/META-INF/additional-spring-configuration-metadata.json");
        assertThat(metadataFile).exists();
    }

    @Test
    void metadata_should_be_written_as_json() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CountryCodeMetadataGenerator.writeMetaData(outputStream, new ConfigurationMetadata());
        String json = outputStream.toString(StandardCharsets.UTF_8.name());
        assertThat(json).contains(
                "\"groups\": []",
                "\"properties\": []",
                "\"hints\": []"
        );
    }
}
