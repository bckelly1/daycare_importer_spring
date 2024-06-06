package com.brian.daycare_importer_spring.controller.api;

import com.brian.daycare_importer_spring.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DaycareApiController {

    @Autowired
    private ImportService importService;

    @GetMapping("/daycare")
    public void triggerDaycareImport() {
        importService.runDaycareSummaryImport();
    }
}
