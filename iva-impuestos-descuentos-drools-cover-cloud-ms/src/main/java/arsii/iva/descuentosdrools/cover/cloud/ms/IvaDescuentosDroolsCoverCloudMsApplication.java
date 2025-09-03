package arsii.iva.descuentosdrools.cover.cloud.ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import arsii.iva.drools.coverage.annotation.EnableDroolsCoverage;

@EnableDroolsCoverage
@SpringBootApplication
public class IvaDescuentosDroolsCoverCloudMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(IvaDescuentosDroolsCoverCloudMsApplication.class, args);
	}

}
