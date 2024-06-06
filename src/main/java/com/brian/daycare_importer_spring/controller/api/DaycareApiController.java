package com.brian.daycare_importer_spring.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DaycareApiController {

    @GetMapping("/daycare")
    public void triggerDaycareImport() {

    }
}
