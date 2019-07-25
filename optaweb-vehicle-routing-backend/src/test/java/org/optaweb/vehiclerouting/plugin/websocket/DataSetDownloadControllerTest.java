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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.service.demo.DemoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataSetDownloadControllerTest {

    @Mock
    private DemoService demoService;
    @InjectMocks
    private DataSetDownloadController controller;

    @Test
    void export() throws IOException {
        // arrange
        String msg = "dummy string";
        when(demoService.exportDataSet()).thenReturn(msg);

        // act
        ResponseEntity<Resource> responseEntity = controller.exportDataSet();

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        HttpHeaders headers = responseEntity.getHeaders();
        // String.length() works here because the message is ASCII
        assertThat(headers.getContentLength()).isEqualTo(msg.length());
        assertThat(headers.getContentType()).isNotNull();
        assertThat(headers.getContentType().toString()).isEqualToIgnoringWhitespace("text/x-yaml;charset=UTF-8");
        assertThat(headers.getContentDisposition()).isNotNull();
        String contentDisposition = headers.getContentDisposition().toString();
        assertThat(contentDisposition).startsWith("attachment;");
        assertThat(contentDisposition).containsPattern("; *filename=\".*\\.yaml\";");
        assertThat(contentDisposition).contains("size=" + msg.length());
        assertThat(contentDisposition).contains("creation-date=");
    }

    @Test
    void content_length_should_be_number_of_bytes() throws IOException {
        // Nice illustration of the problem: https://sankhs.com/2016/03/17/content-length-http-headers/
        // If the content-length header is less than number of bytes, part of the response body thrown away!
        // So if we sent "অhello" with content-length: 6, the client (browser) would only present "অhel".

        // arrange
        String msg = "অ";
        when(demoService.exportDataSet()).thenReturn(msg);

        // act
        ResponseEntity<Resource> responseEntity = controller.exportDataSet();

        // assert
        assertThat(responseEntity.getHeaders().getContentLength()).isEqualTo(3);
    }
}
