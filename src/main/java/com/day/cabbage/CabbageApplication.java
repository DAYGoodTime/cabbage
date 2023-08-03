package com.day.cabbage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication

public class CabbageApplication {

    public static void main(String[] args) {
        SpringApplication.run(CabbageApplication.class, args);
    }

}
