package arsii.iva.descuentosdrools.cloud.ms.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
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
class RulesIntegrationWithCoverageTest4 {
	
	/*
	  ¿Quieres que en lugar de hardcodear las reglas esperadas (ALL_DESCUENTO_RULES, ALL_IMPUESTO_RULES), te arme un método que las obtenga dinámicamente desde los .drl para no tener que mantener esa lista manualmente?
	  Así evitas tener que mantener listas manuales de reglas y el reporte siempre reflejará lo que realmente tienes en tus .drl.

	  Drools expone el KnowledgeBase (KieBase) que contiene todos los Rule cargados en una KieSession.
	  Podemos recorrerlo y obtener dinámicamente los nombres de las reglas en cada paquete.
      Aquí te dejo la versión corregida y mejorada de tu test con extracción automática de reglas:
      
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
        Cliente cliente = new Cliente("Ana", 70, 200.0);

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
    static void printCoverageReport(@Autowired @Qualifier("descuentoSession") KieSession descuentoSession,
                                    @Autowired @Qualifier("impuestoSession") KieSession impuestoSession) {

        Set<String> fired = coverageListener.getFiredRules();

        System.out.println("\n========= 📊 Cobertura de reglas Drools =========");

        // 📌 Reporte dinámico por cada paquete
        reportCoverageForSession("rules.descuentos", descuentoSession, fired);
        reportCoverageForSession("rules.impuestos", impuestoSession, fired);
    }

    private static void reportCoverageForSession(String packageName, KieSession session, Set<String> firedRules) {
        KieBase kieBase = session.getKieBase();

        // 🔎 Obtener todas las reglas de ese paquete
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
    }
}
