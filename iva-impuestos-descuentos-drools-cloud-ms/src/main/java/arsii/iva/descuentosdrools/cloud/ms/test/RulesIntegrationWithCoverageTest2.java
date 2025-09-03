package arsii.iva.descuentosdrools.cloud.ms.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class RulesIntegrationWithCoverageTest2 {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 🔹 Inyectamos ambas sesiones de Drools explícitamente
    @Autowired
    @Qualifier("descuentoSession")
    private KieSession descuentoSession;

    @Autowired
    @Qualifier("impuestoSession")
    private KieSession impuestoSession;

    private static RuleCoverageListener coverageListener = new RuleCoverageListener();

    @BeforeEach
    void setupListener() {
        // 🔹 Limpia y vuelve a registrar listener en ambas sesiones
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

        System.out.println("========= 📊 Cobertura de reglas Drools =========");
        fired.forEach(rule -> System.out.println("✅ Regla disparada: " + rule));

        // Aquí podrías validar cobertura completa de reglas
        // assertThat(fired).containsExactlyInAnyOrder("ReglaDescuentoEdad", "ReglaImpuestoIVA", ...);
    }
}
