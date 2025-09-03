package arsii.iva.drools.coverage.listener;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DroolsCoverageListener extends DefaultAgendaEventListener {

    private final Set<String> firedRules = ConcurrentHashMap.newKeySet();

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        firedRules.add(event.getMatch().getRule().getName());
    }

    public Set<String> getFiredRules() {
        return firedRules;
    }
}
