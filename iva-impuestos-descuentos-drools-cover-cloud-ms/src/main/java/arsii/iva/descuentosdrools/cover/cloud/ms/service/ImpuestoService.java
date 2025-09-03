package arsii.iva.descuentosdrools.cover.cloud.ms.service;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import arsii.iva.descuentosdrools.cover.cloud.ms.model.Factura;

@Service
public class ImpuestoService {

    private final KieSession impuestoSession;

    public ImpuestoService(@Qualifier("impuestoSession") KieSession impuestoSession) {
        this.impuestoSession = impuestoSession;
    }

    public Factura calcularIVA(Factura factura) {
        try {
            impuestoSession.insert(factura);
            impuestoSession.fireAllRules();
        } finally {
        	impuestoSession.dispose(); // siempre cerrar para evitar fugas de memoria
        }
        return factura;
    }
}
