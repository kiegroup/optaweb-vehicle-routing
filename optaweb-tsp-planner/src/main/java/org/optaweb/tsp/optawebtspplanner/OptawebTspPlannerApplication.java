package org.optaweb.tsp.optawebtspplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OptawebTspPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OptawebTspPlannerApplication.class, args);
    }
}
