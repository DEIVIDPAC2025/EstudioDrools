package arsii.iva.drools.coverage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DroolsCoverageSpringBootStarterApplication {

	/*
	Flujo del starter

	@EnableDroolsCoverage

	Es una anotación que habilita la autoconfiguración.

	Básicamente importa la clase DroolsCoverageAutoConfiguration.

	DroolsCoverageAutoConfiguration

	Registra dos beans en el contexto de Spring:

	DroolsCoverageListener

	DroolsCoverageReporter

	También asocia el listener a todas las sesiones de Drools (KieSession).

	DroolsCoverageListener

	Se engancha a Drools y captura cada regla que se dispara (afterMatchFired).

	Va guardando los nombres de las reglas ejecutadas en un Set<String>.

	DroolsCoverageReporter

	Tiene el método generateReport().

	Este método toma lo que acumuló el listener y lo transforma en un reporte (ej: JSON, CSV, etc., según tu implementación).
	*/
	public static void main(String[] args) {
		SpringApplication.run(DroolsCoverageSpringBootStarterApplication.class, args);
	}

}
