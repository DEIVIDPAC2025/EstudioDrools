package arsii.iva.descuentosdrools.cloud.ms.config;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

    private KieContainer buildKieContainer(String... rulePaths) {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        // Cargar cada archivo .drl del classpath
        for (String path : rulePaths) {
            kieFileSystem.write(
                kieServices.getResources().newClassPathResource(path, getClass())
            );
        }

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Error compilando reglas: " + kieBuilder.getResults());
        }

        return kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }

    // Sesión para reglas de descuentos
    @Bean(name = "descuentoSession")
    public KieSession descuentoSession() {
        return buildKieContainer("rules/descuentos/descuento.drl").newKieSession();
    }

    // Sesión para reglas de impuestos
    @Bean(name = "impuestoSession")
    public KieSession impuestoSession() {
        return buildKieContainer("rules/impuestos/impuesto.drl").newKieSession();
    }
}
