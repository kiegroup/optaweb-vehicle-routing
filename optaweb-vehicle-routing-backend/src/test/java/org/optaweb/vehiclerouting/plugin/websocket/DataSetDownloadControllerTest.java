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

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaweb.vehiclerouting.service.demo.DemoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataSetDownloadControllerTest {

    @Mock
    private DemoService demoService;
    @InjectMocks
    private DataSetDownloadController controller;

    @Test
    public void export() throws IOException {
        // arrange
        String msg = "dummy string";
        when(demoService.exportDataSet()).thenReturn(msg);

        // act
        ResponseEntity<Resource> responseEntity = controller.exportDataSet();

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        HttpHeaders headers = responseEntity.getHeaders();
        assertThat(headers.getContentLength()).isEqualTo(msg.length());
        assertThat(headers.getContentType()).isNotNull();
        assertThat(headers.getContentType().toString()).isEqualToIgnoringWhitespace("text/x-yaml;charset=UTF-16");
        assertThat(headers.getContentDisposition()).isNotNull();
        String contentDisposition = headers.getContentDisposition().toString();
        assertThat(contentDisposition).startsWith("attachment;");
        assertThat(contentDisposition).containsPattern("; *filename=\".*\\.yaml\";");
        assertThat(contentDisposition).contains("size=" + msg.length());
        assertThat(contentDisposition).contains("creation-date=");
    }
}
