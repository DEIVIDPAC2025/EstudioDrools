package arsii.iva.drools.coverage.report;

import com.fasterxml.jackson.databind.ObjectMapper;

import arsii.iva.drools.coverage.listener.DroolsCoverageListener;

import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieSession;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

public class DroolsCoverageReporter {

    private final DroolsCoverageListener listener;
    private final List<KieSession> sessions;
    private final ObjectMapper mapper = new ObjectMapper();

    public DroolsCoverageReporter(DroolsCoverageListener listener, List<KieSession> sessions) {
        this.listener = listener;
        this.sessions = sessions;
    }

    @PreDestroy
    public void generateReport() throws Exception {
        Set<String> fired = listener.getFiredRules();
        List<Map<String, Object>> reportData = new ArrayList<>();

        for (KieSession session : sessions) {
            KieBase kieBase = session.getKieBase();

            kieBase.getKiePackages().forEach(kp -> {
                Set<String> allRules = kp.getRules().stream()
                        .map(Rule::getName).collect(Collectors.toSet());

                Set<String> firedRules = fired.stream()
                        .filter(allRules::contains)
                        .collect(Collectors.toSet());

                allRules.forEach(rule -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("package", kp.getName());
                    row.put("rule", rule);
                    row.put("fired", firedRules.contains(rule));
                    reportData.add(row);
                });
            });
        }

        File folder = new File("target/coverage");
        folder.mkdirs();

        // JSON
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(folder, "drools-coverage.json"), reportData);

        // CSV
        try (FileWriter writer = new FileWriter(new File(folder, "drools-coverage.csv"))) {
            writer.write("package,rule,fired\n");
            for (Map<String, Object> row : reportData) {
                writer.write(row.get("package") + "," + row.get("rule") + "," + row.get("fired") + "\n");
            }
        }
    }
}

