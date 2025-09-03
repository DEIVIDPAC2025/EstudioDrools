package arsii.iva.descuentosdrools.cloud.ms.test;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import arsii.iva.descuentosdrools.cloud.ms.model.Factura;
import arsii.iva.descuentosdrools.cloud.ms.service.ImpuestoService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ImpuestoRulesTest {

    @Autowired
    private ImpuestoService impuestoService;

    @Test
    void cuandoFacturaConMonto_calculaIVA() {
        Factura factura = new Factura("F001", 200.0);

        Factura resultado = impuestoService.calcularIVA(factura);

        assertEquals(38.0, resultado.getIva(), 0.01,
                "El IVA debe ser 19% del monto");
    }
}
