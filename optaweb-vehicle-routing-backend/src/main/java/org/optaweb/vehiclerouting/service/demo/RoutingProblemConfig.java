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

package org.optaweb.vehiclerouting.service.demo;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration bean that produces the list of available routing problem data sets.
 */
@Dependent
class RoutingProblemConfig {

    private static final Logger logger = LoggerFactory.getLogger(RoutingProblemConfig.class);
    private final DemoProperties demoProperties;
    private final DataSetMarshaller dataSetMarshaller;

    @Inject
    RoutingProblemConfig(DemoProperties demoProperties, DataSetMarshaller dataSetMarshaller) {
        this.demoProperties = demoProperties;
        this.dataSetMarshaller = dataSetMarshaller;
    }

    @Produces
    RoutingProblemList routingProblems() {
        Stream<RoutingProblem> allProblems = Stream.concat(classPathProblems(), dataSetDirProblems());
        return new RoutingProblemList(allProblems);
    }

    private Stream<RoutingProblem> classPathProblems() {
        return Stream.of(belgiumReader()).map(dataSetMarshaller::unmarshal);
    }

    private Stream<RoutingProblem> dataSetDirProblems() {
        return dataSetDir().map(dir -> collectProblems(dir).stream()).orElse(Stream.empty());
    }

    private List<RoutingProblem> collectProblems(Path dataSetDirPath) {
        try (Stream<Path> dataSetPaths = Files.list(dataSetDirPath)) {
            return dataSetPaths
                    .map(Path::toFile)
                    .filter(file -> file.getName().endsWith(".yaml") && file.exists() && file.canRead())
                    .map(file -> {
                        try {
                            return new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                        } catch (FileNotFoundException e) {
                            logger.error("Problem with dataset file {}", file, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    // TODO make unmarshalling exception checked, catch it and ignore broken files
                    .map(dataSetMarshaller::unmarshal)
                    // Returning the stream here has no point because the stream is always closed by the try-with-resources.
                    .collect(toList());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot list directory " + dataSetDirPath, e);
        }
    }

    private Optional<Path> dataSetDir() {
        // TODO watch the dir (and make this a service that has local/data resource as a dependency -> is testable)
        Optional<String> dataSetDirProperty = demoProperties.dataSetDir();
        if (!dataSetDirProperty.isPresent()) {
            logger.info("Data set directory (app.demo.data-set-dir) is not set.");
            return Optional.empty();
        }
        Path dataSetDirPath = Paths.get(dataSetDirProperty.get());
        if (!isReadableDir(dataSetDirPath)) {
            logger.warn(
                    "Data set directory '{}' doesn't exist or cannot be read. No external data sets will be loaded.",
                    dataSetDirPath.toAbsolutePath());
            return Optional.empty();
        }
        return Optional.of(dataSetDirPath);
    }

    private static Reader belgiumReader() {
        return new InputStreamReader(
                DemoService.class.getResourceAsStream("belgium-cities.yaml"),
                StandardCharsets.UTF_8);
    }

    private static boolean isReadableDir(Path path) {
        File file = path.toFile();
        return file.exists() && file.canRead() && file.isDirectory();
    }
}
