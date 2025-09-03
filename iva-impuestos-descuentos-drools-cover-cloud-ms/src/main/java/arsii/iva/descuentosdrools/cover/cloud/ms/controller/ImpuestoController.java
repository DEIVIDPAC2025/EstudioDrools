package arsii.iva.descuentosdrools.cover.cloud.ms.controller;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import arsii.iva.descuentosdrools.cover.cloud.ms.model.Factura;

@RestController
@RequestMapping("/api")
public class ImpuestoController {

    private final KieSession impuestoSession;

    public ImpuestoController(@Qualifier("impuestoSession") KieSession impuestoSession) {
        this.impuestoSession = impuestoSession;
    }

    @PostMapping("/impuesto")
    public Factura calcularImpuesto(@RequestBody Factura factura) {
        impuestoSession.insert(factura);
        impuestoSession.fireAllRules();
        return factura;
    }
}


