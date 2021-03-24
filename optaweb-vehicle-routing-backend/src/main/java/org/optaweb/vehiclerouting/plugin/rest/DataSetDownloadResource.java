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

package org.optaweb.vehiclerouting.plugin.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.optaweb.vehiclerouting.service.demo.DemoService;

/**
 * Serves the current data set as a downloadable YAML file.
 */
@Path("api/dataset/export")
@Produces("text/x-yaml")
public class DataSetDownloadResource {

    private final DemoService demoService;

    DataSetDownloadResource(DemoService demoService) {
        this.demoService = demoService;
    }

    @GET
    public Response exportDataSet() throws IOException {
        String dataSet = demoService.exportDataSet();
        byte[] dataSetBytes = dataSet.getBytes(StandardCharsets.UTF_8);
        try (InputStream is = new ByteArrayInputStream(dataSetBytes)) {
            return Response.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"vrp_data_set.yaml\"")
                    .header(HttpHeaders.CONTENT_LENGTH, dataSetBytes.length)
                    .type(new MediaType("text", "x-yaml", StandardCharsets.UTF_8.name()))
                    .entity(is)
                    .build();
        }
    }

    // TODO exception handler
}
