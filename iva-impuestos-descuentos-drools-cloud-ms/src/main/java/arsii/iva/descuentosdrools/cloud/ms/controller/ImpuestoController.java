package arsii.iva.descuentosdrools.cloud.ms.controller;

import arsii.iva.descuentosdrools.cloud.ms.model.Factura;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

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
