package arsii.iva.descuentosdrools.cover.cloud.ms.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import arsii.iva.descuentosdrools.cover.cloud.ms.model.Cliente;
import arsii.iva.descuentosdrools.cover.cloud.ms.model.Factura;


@SpringBootTest
@AutoConfigureMockMvc
class RulesIntegrationWithCoverageTest {
	
	/*
	Perfecto 🚀, ya con la imagen veo tu RulesIntegrationWithCoverageTest y tu proyecto 
	corriendo junto con drools-coverage-spring-boot-starter en el Boot Dashboard.
	La idea es sacar del test todo el código manual de enganche del RuleCoverageListener y 
	dejar que el starter lo haga automáticamente vía @EnableDroolsCoverage.
	
	¿Quieres que también te adapte el test de descuentos (testDescuento) para que quede parametrizado igual que este y evitar repetir código?
	Así evitamos repetir código y dejamos ambos tests (descuento e impuesto) parametrizados con JUnit 5.

      Aquí tienes tu clase completa con ambos métodos adaptados:
      
      No hay código repetido en testDescuento.

Cada test es ejecutado N veces automáticamente según los casos del @CsvSource.

La cobertura de reglas (DroolsCoverage) también se beneficia porque verás que todas las ramas de reglas se ejecutan.

¿Quieres que además le agreguemos un @DisplayName dinámico en los tests parametrizados, para que al correrlos en JUnit te muestre algo como "Cliente 21 años obtiene 10% de descuento" o "Factura F002 aplica IVA=95.0"?
    con JUnit 5 podemos usar @DisplayName y @DisplayNameGeneration, pero para algo más descriptivo en tests parametrizados lo mejor es @ParameterizedTest(name = "...").
    
      Ejemplo de cómo se vería al ejecutar:

✅ Cliente David Pacheco, edad=21, monto=4000.0 → descuento esperado=400.0

✅ Cliente Jose Diaz, edad=30, monto=4000.0 → descuento esperado=1200.0

✅ Factura F002, monto=500.0 → IVA esperado=95.0
      
      
    */
	
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

   /*
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
    */

    /*
    @Test
    void testImpuesto() throws Exception {
    	 // Factura clcula impuesto-> 19% de 500.0 = 95.0
    	Factura factura = new Factura("F002", 500.0);
        mockMvc.perform(post("/api/impuesto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(factura)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iva").value(95.0));
    }
    */
    
    @ParameterizedTest(name = "Cliente {0}, edad={1}, monto={2} → descuento esperado={3}")
    @CsvSource({
        // nombre, edad, monto, descuento esperado
        "David Pacheco, 21, 4000.0, 400.0",    // < 25 años → 10%
        "Jose Diaz, 30, 4000.0, 1200.0",       // 25-59 años → 30%
        "Gonzalo Martinez, 61, 4000.0, 2000.0" // >= 60 años → 50%
    })
    void testDescuentoParametrizado(String nombre, int edad, double compra, double descuentoEsperado) throws Exception {
        Cliente cliente = new Cliente(nombre, edad, compra);

        mockMvc.perform(post("/api/descuento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value(nombre))
                .andExpect(jsonPath("$.edad").value(edad))
                .andExpect(jsonPath("$.compra").value(compra))
                .andExpect(jsonPath("$.descuento").value(descuentoEsperado));
    }

    @ParameterizedTest(name = "Factura {0}, monto={1} → IVA esperado={2}")
    @CsvSource({
        // numero factura, monto, iva esperado
        "F001, 100.0, 19.0",
        "F002, 500.0, 95.0",
        "F003, 1000.0, 190.0",
        "F004, 2000.0, 380.0"
    })
    void testImpuestoParametrizado(String numero, double monto, double ivaEsperado) throws Exception {
        Factura factura = new Factura(numero, monto);

        mockMvc.perform(post("/api/impuesto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(factura)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(numero))
                .andExpect(jsonPath("$.monto").value(monto))
                .andExpect(jsonPath("$.iva").value(ivaEsperado));
    }
 
}
