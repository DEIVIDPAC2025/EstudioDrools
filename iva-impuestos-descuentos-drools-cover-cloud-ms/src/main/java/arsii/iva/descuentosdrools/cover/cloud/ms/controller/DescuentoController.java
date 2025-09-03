package arsii.iva.descuentosdrools.cover.cloud.ms.controller;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import arsii.iva.descuentosdrools.cover.cloud.ms.model.Cliente;


@RestController
@RequestMapping("/api")
public class DescuentoController {
//
    private final KieSession descuentoSession;

    public DescuentoController(@Qualifier("descuentoSession") KieSession descuentoSession) {
        this.descuentoSession = descuentoSession;
    }

    @PostMapping("/descuento")
    public Cliente calcularDescuento(@RequestBody Cliente cliente) {
        descuentoSession.insert(cliente);
        descuentoSession.fireAllRules();
        return cliente;
    }
}



