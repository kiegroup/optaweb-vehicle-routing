package org.optaweb.vehiclerouting.plugin.rest;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.service.demo.DemoService;

@ExtendWith(MockitoExtension.class)
class DemoResourceTest {

    @Mock
    private DemoService demoService;
    @InjectMocks
    private DemoResource demoResource;

    @Test
    void demo() {
        String problemName = "xy";
        demoResource.loadDemo(problemName);
        verify(demoService).loadDemo(problemName);
    }
}
