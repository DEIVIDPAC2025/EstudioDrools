package arsii.iva.descuentosdrools.cloud.ms.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class RulesIntegrationWithCoverageTest3 {
   
	/*
	Perfecto 🙌, vamos a extender el reporte de cobertura para que no solo muestre las reglas disparadas, sino también las que no se dispararon dentro de cada paquete (rules.descuentos y rules.impuestos).
	La idea es:
	Mantener una lista de todas las reglas que existen (esperadas).
	Al final del test, comparar con las que efectivamente se dispararon (coverageListener.getFiredRules()).
	Mostrar un resumen por paquete.
	Aquí te dejo una versión extendida de tu clase:
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

	    // 🔹 Todas las reglas que esperamos (hardcoded o podrías cargarlas dinámicamente)
	    private static final Set<String> ALL_DESCUENTO_RULES = new HashSet<>(Arrays.asList(
	            "ReglaDescuentoEdad",
	            "ReglaDescuentoClienteVIP"
	    ));

	    private static final Set<String> ALL_IMPUESTO_RULES = new HashSet<>(Arrays.asList(
	            "ReglaImpuestoIVA",
	            "ReglaImpuestoExtra"
	    ));

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
	    static void printCoverageReport() {
	        Set<String> fired = coverageListener.getFiredRules();

	        System.out.println("\n========= 📊 Cobertura de reglas Drools =========");

	        // 📌 Reporte para DESCUENTOS
	        System.out.println("\n--- Paquete: rules.descuentos ---");
	        reportCoverageForPackage(ALL_DESCUENTO_RULES, fired);

	        // 📌 Reporte para IMPUESTOS
	        System.out.println("\n--- Paquete: rules.impuestos ---");
	        reportCoverageForPackage(ALL_IMPUESTO_RULES, fired);
	    }

	    private static void reportCoverageForPackage(Set<String> allRules, Set<String> firedRules) {
	        Set<String> firedInPackage = new HashSet<>(firedRules);
	        firedInPackage.retainAll(allRules);

	        Set<String> notFired = new HashSet<>(allRules);
	        notFired.removeAll(firedInPackage);

	        firedInPackage.forEach(rule -> System.out.println("✅ Regla disparada: " + rule));
	        notFired.forEach(rule -> System.out.println("⚠️ Regla NO disparada: " + rule));

	        double coverage = (double) firedInPackage.size() / allRules.size() * 100.0;
	        System.out.printf("Cobertura: %.2f%% (%d/%d reglas)\n",
	                coverage, firedInPackage.size(), allRules.size());
	    }
}