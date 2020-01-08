/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.neovisionaries.i18n.CountryCode;
import org.springframework.boot.configurationprocessor.metadata.ConfigurationMetadata;
import org.springframework.boot.configurationprocessor.metadata.ItemHint;
import org.springframework.boot.configurationprocessor.metadata.ItemMetadata;
import org.springframework.boot.configurationprocessor.metadata.JsonMarshaller;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Generates Spring Boot
 * <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/configuration-metadata.html">
 * Configuration Metadata
 * </a>
 * for {@link RegionProperties#getCountryCodes()} property.
 */
public class CountryCodeMetadataGenerator {

    /**
     * Generate {@code app.region.country-codes} Configuration Metadata from {@link CountryCode}.
     * @param args no arguments are used
     * @throws IOException when the metadata file cannot be written
     * @throws NoSuchFieldException if {@link RegionProperties} inspection fails
     * @throws NoSuchMethodException if {@link RegionProperties} inspection fails
     */
    public static void main(String[] args) throws IOException, NoSuchFieldException, NoSuchMethodException {
        File metadataFile = prepareMetadataFile(moduleRoot());
        try (FileOutputStream outputStream = new FileOutputStream(metadataFile)) {
            writeMetaData(outputStream, metadata());
        }
    }

    static File moduleRoot() {
        URL resource = CountryCodeMetadataGenerator.class.getResource(".");
        if (!resource.toString().contains("/target/")) {
            throw new IllegalStateException(resource.toString());
        }
        File file = new File(resource.getFile());
        while (!file.getName().equals("target")) {
            file = file.getParentFile();
        }
        return file.getParentFile();
    }

    static File prepareMetadataFile(File module) throws IOException {
        File metaInfDir = new File(module, "src/main/resources/META-INF/");
        if (!metaInfDir.exists() && !metaInfDir.mkdirs()) {
            throw new IllegalStateException("Cannot create directory: " + metaInfDir);
        }
        File metadataFile = new File(metaInfDir, "additional-spring-configuration-metadata.json");
        if (!metadataFile.exists() && !metadataFile.createNewFile()) {
            throw new IllegalStateException("Cannot create file: " + metadataFile);
        }
        if (!metadataFile.canWrite()) {
            throw new IllegalStateException("Cannot write to file: " + metadataFile);
        }
        return metadataFile;
    }

    static ConfigurationMetadata metadata() throws NoSuchFieldException, NoSuchMethodException {
        String fieldName = "countryCodes";
        ItemMetadata property = ItemMetadata.newProperty(
                RegionProperties.class.getAnnotation(ConfigurationProperties.class).value(),
                fieldName,
                RegionProperties.class.getDeclaredField(fieldName).getGenericType().toString(),
                RegionProperties.class.getName(),
                RegionProperties.class.getDeclaredMethod("getCountryCodes").getName() + "()",
                null, null, null
        );

        List<ItemHint.ValueHint> valueHints = Arrays.stream(CountryCode.values())
                .filter(countryCode -> countryCode != CountryCode.UNDEFINED)
                .map(countryCode -> new ItemHint.ValueHint(countryCode.getAlpha2(), countryCode.getName() + "."))
                .collect(Collectors.toList());
        ItemHint itemHint = new ItemHint(property.getName(), valueHints, null);

        ConfigurationMetadata configurationMetadata = new ConfigurationMetadata();
        configurationMetadata.add(property);
        configurationMetadata.add(itemHint);
        return configurationMetadata;
    }

    static void writeMetaData(OutputStream outputStream, ConfigurationMetadata metadata) throws IOException {
        new JsonMarshaller().write(metadata, outputStream);
    }
}
