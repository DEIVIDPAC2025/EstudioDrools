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
class RulesIntegrationWithCoverageTest6 {

	/*
	Aquí tienes tu RulesIntegrationWithCoverageTest6 corregido 
	para que dispare todas las reglas de descuento y reporte 100% de cobertura 
	en rules.descuentos:
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
        // Cliente menor de 25 años -> 10% de 4000 = 400
        Cliente cliente = new Cliente("David Pacheco", 21, 4000.0);
        mockMvc.perform(post("/api/descuento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descuento").value(400.0));

        // Cliente adulto joven 25-59 años -> 30% de 4000 = 1200
        cliente = new Cliente("Jose Diaz", 30, 4000.0);
        mockMvc.perform(post("/api/descuento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descuento").value(1200.0));

        // Cliente mayor de 60 años -> 50% de 4000 = 2000
        cliente = new Cliente("Gonzalo Martinez", 61, 4000.0);
        mockMvc.perform(post("/api/descuento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descuento").value(2000.0));
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

        List<Map<String, Object>> reportData = new ArrayList<>();
        reportData.addAll(reportCoverageForSession("rules.descuentos", descuentoSession, fired));
        reportData.addAll(reportCoverageForSession("rules.impuestos", impuestoSession, fired));

        // Crear carpeta target/coverage si no existe
        File folder = new File("target/coverage");
        if (!folder.exists()) folder.mkdirs();

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
