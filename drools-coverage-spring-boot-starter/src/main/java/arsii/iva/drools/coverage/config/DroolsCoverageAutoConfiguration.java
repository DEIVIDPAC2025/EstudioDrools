package arsii.iva.drools.coverage.config;

import java.util.List;

import javax.annotation.PostConstruct;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import arsii.iva.drools.coverage.listener.DroolsCoverageListener;
import arsii.iva.drools.coverage.report.DroolsCoverageReporter;

@Configuration
@ConditionalOnClass(KieSession.class)
public class DroolsCoverageAutoConfiguration {

    private final List<KieSession> kieSessions;
    private DroolsCoverageListener listener;

    public DroolsCoverageAutoConfiguration(ObjectProvider<List<KieSession>> kieSessions) {
        this.kieSessions = kieSessions.getIfAvailable(List::of);
    }

    // 🔹 Bean simple, sin depender de la clase de configuración
    @Bean
    @ConditionalOnMissingBean
    public DroolsCoverageListener droolsCoverageListener() {
        return new DroolsCoverageListener();
    }

    // 🔹 Registro del listener en todas las sesiones, Spring inyecta el bean aquí
    @PostConstruct
    public void registerListener() {
       listener = droolsCoverageListener();
       kieSessions.forEach(session -> session.addEventListener(listener));
    }

    // 🔹 Reporter depende explícitamente del listener ya creado
    @Bean
    public DroolsCoverageReporter droolsCoverageReporter() {
        return new DroolsCoverageReporter(listener, kieSessions);
    }
}
