package arsii.iva.descuentosdrools.cloud.ms.service;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import arsii.iva.descuentosdrools.cloud.ms.model.Cliente;

@Service
public class DescuentoService {

    private final KieSession descuentoSession;

    public DescuentoService(@Qualifier("descuentoSession") KieSession descuentoSession) {
        this.descuentoSession = descuentoSession;
    }

    public Cliente aplicarDescuento(Cliente cliente) {
        descuentoSession.insert(cliente);
        descuentoSession.fireAllRules();
        return cliente;
    }
}

