package arsii.iva.descuentosdrools.cover.cloud.ms.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import arsii.iva.drools.coverage.report.DroolsCoverageReporter;

@RestController
@RequestMapping("/coverage")
public class CoverageController {

    private final DroolsCoverageReporter reporter;

    public CoverageController(DroolsCoverageReporter reporter) {
        this.reporter = reporter;
    }

    @PostMapping("/generate")
    public String generateReport() {
        try {
			reporter.generateReport();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "Reporte de cobertura generado en target/coverage/";
    }
}

