package arsii.iva.drools.coverage.annotation;


import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(arsii.iva.drools.coverage.config.DroolsCoverageAutoConfiguration.class)
public @interface EnableDroolsCoverage {
}
