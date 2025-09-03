package arsii.iva.descuentosdrools.cloud.ms.test;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import arsii.iva.descuentosdrools.cloud.ms.model.Cliente;
import arsii.iva.descuentosdrools.cloud.ms.service.DescuentoService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DescuentoRulesTest {

    @Autowired
    private DescuentoService descuentoService;

    @Test
    void cuandoClienteMayor60_aplicaDescuento() {
        Cliente cliente = new Cliente("Juan", 65, 100.0);

        Cliente resultado = descuentoService.aplicarDescuento(cliente);

        assertEquals(20.0, resultado.getDescuento(), 0.01,
                "El descuento debe ser 20% de la compra");
    }

    @Test
    void cuandoClienteMenor60_noAplicaDescuento() {
        Cliente cliente = new Cliente("Pedro", 40, 100.0);

        Cliente resultado = descuentoService.aplicarDescuento(cliente);

        assertEquals(0.0, resultado.getDescuento(), 0.01,
                "No debe aplicar descuento");
    }
}
