package org.optaweb.vehiclerouting.service.demo;

import java.util.Optional;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "app.demo")
public interface DemoProperties {

    /**
     * Directory with demo data sets.
     */
    Optional<String> dataSetDir();
}
