package arsii.iva.descuentosdrools.cloud.ms.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
//import org.kie.api.runtime.KieBase;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import arsii.iva.descuentosdrools.cloud.ms.model.Cliente;
import arsii.iva.descuentosdrools.cloud.ms.model.Factura;

@SpringBootTest
@AutoConfigureMockMvc
class RulesIntegrationWithCoverageTest5 {

	/*
	¿Quieres que además este reporte se guarde en un archivo (JSON o CSV) para integrarlo en tu pipeline de CI/CD (ej: Jenkins, GitLab, SonarQube)?
	Eso le da trazabilidad y lo puedes integrar con Jenkins/GitLab/Sonar.
	Te propongo que además del System.out.println guardemos el reporte en JSON y CSV en la carpeta target/coverage/.
	*/
	
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("descuentoSession")
    private KieSession descuentoSession;

    @Autowired
    @Qualifier("impuestoSession")
    private KieSession impuestoSession;

    private static RuleCoverageListener coverageListener = new RuleCoverageListener();

    @BeforeEach
    void setupListener() {
        descuentoSession.removeEventListener(coverageListener);
        impuestoSession.removeEventListener(coverageListener);

        coverageListener = new RuleCoverageListener();

        descuentoSession.addEventListener(coverageListener);
        impuestoSession.addEventListener(coverageListener);
    }

    @Test
    void testDescuento() throws Exception {
        Cliente cliente = new Cliente("David Pacheco", 21, 4000.0);

        mockMvc.perform(post("/api/descuento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descuento").value(40.0));
    }

    @Test
    void testImpuesto() throws Exception {
        Factura factura = new Factura("F002", 500.0);

        mockMvc.perform(post("/api/impuesto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(factura)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iva").value(95.0));
    }

    @AfterAll
    static void printCoverageReport(
            @Autowired @Qualifier("descuentoSession") KieSession descuentoSession,
            @Autowired @Qualifier("impuestoSession") KieSession impuestoSession,
            @Autowired ObjectMapper mapper) throws IOException {

        Set<String> fired = coverageListener.getFiredRules();

        System.out.println("\n========= 📊 Cobertura de reglas Drools =========");

        // Reporte dinámico por cada paquete
        List<Map<String, Object>> reportData = new ArrayList<>();
        reportData.addAll(reportCoverageForSession("rules.descuentos", descuentoSession, fired));
        reportData.addAll(reportCoverageForSession("rules.impuestos", impuestoSession, fired));

        // Crear carpeta target/coverage si no existe
        File folder = new File("target/coverage");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Guardar JSON
        File jsonFile = new File(folder, "drools-coverage.json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, reportData);

        // Guardar CSV
        File csvFile = new File(folder, "drools-coverage.csv");
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("package,rule,fired\n");
            for (Map<String, Object> row : reportData) {
                writer.write(row.get("package") + "," + row.get("rule") + "," + row.get("fired") + "\n");
            }
        }

        System.out.println("📁 Reportes guardados en: " + folder.getAbsolutePath());
    }

    private static List<Map<String, Object>> reportCoverageForSession(
            String packageName, KieSession session, Set<String> firedRules) {

        KieBase kieBase = session.getKieBase();

        Set<String> allRules = kieBase.getKiePackages().stream()
                .filter(kp -> kp.getName().equals(packageName))
                .flatMap(kp -> kp.getRules().stream())
                .map(Rule::getName)
                .collect(Collectors.toSet());

        Set<String> firedInPackage = new HashSet<>(firedRules);
        firedInPackage.retainAll(allRules);

        Set<String> notFired = new HashSet<>(allRules);
        notFired.removeAll(firedInPackage);

        System.out.println("\n--- Paquete: " + packageName + " ---");
        firedInPackage.forEach(rule -> System.out.println("✅ Regla disparada: " + rule));
        notFired.forEach(rule -> System.out.println("⚠️ Regla NO disparada: " + rule));

        double coverage = allRules.isEmpty() ? 100.0 :
                (double) firedInPackage.size() / allRules.size() * 100.0;

        System.out.printf("Cobertura: %.2f%% (%d/%d reglas)\n",
                coverage, firedInPackage.size(), allRules.size());

        // Construir datos para JSON/CSV
        List<Map<String, Object>> rows = new ArrayList<>();
        for (String rule : allRules) {
            Map<String, Object> row = new HashMap<>();
            row.put("package", packageName);
            row.put("rule", rule);
            row.put("fired", firedInPackage.contains(rule));
            rows.add(row);
        }
        return rows;
    }
}
