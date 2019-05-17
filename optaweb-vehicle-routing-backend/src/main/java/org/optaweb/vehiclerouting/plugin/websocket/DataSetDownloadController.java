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

package org.optaweb.vehiclerouting.plugin.websocket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

import org.optaweb.vehiclerouting.service.demo.DemoService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Serves the current data set as a downloadable YAML file.
 */
@Controller
class DataSetDownloadController {

    private final DemoService demoService;

    DataSetDownloadController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping(value = "/dataset/export")
    @ResponseBody
    public ResponseEntity<Resource> exportDataSet() throws IOException {
        String dataSet = demoService.exportDataSet();
        byte[] dataSetBytes = dataSet.getBytes(StandardCharsets.UTF_8);
        try (InputStream is = new ByteArrayInputStream(dataSetBytes)) {
            HttpHeaders headers = new HttpHeaders();
            ContentDisposition attachment = ContentDisposition.builder("attachment")
                    .filename("vrp_data_set.yaml")
                    .creationDate(ZonedDateTime.now())
                    .size((long) dataSetBytes.length)
                    .build();
            headers.setContentDisposition(attachment);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(dataSetBytes.length)
                    .contentType(new MediaType("text", "x-yaml", StandardCharsets.UTF_8))
                    .body(new InputStreamResource(is));
        }
    }

    // TODO exception handler
}
