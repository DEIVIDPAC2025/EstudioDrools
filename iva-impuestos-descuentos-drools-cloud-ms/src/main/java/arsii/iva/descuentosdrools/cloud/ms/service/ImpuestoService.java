package arsii.iva.descuentosdrools.cloud.ms.service;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import arsii.iva.descuentosdrools.cloud.ms.model.Factura;

@Service
public class ImpuestoService {

    private final KieSession impuestoSession;

    public ImpuestoService(@Qualifier("impuestoSession") KieSession impuestoSession) {
        this.impuestoSession = impuestoSession;
    }

    public Factura calcularIVA(Factura factura) {
        impuestoSession.insert(factura);
        impuestoSession.fireAllRules();
        return factura;
    }
}
