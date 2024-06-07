package com.brian.daycare_importer_spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class DaycareImporterSpringApplication {

    public static void main(String[] args) {
        log.info("Starting Daycare importer in directory {}", System.getProperty("user.dir"));
        SpringApplication.run(DaycareImporterSpringApplication.class, args);
    }

}
