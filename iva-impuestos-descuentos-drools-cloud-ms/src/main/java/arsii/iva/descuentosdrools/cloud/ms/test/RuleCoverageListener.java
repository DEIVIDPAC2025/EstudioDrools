package arsii.iva.descuentosdrools.cloud.ms.test;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;

import java.util.HashSet;
import java.util.Set;

public class RuleCoverageListener implements AgendaEventListener {

    private final Set<String> firedRules = new HashSet<>();

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        firedRules.add(event.getMatch().getRule().getName());
    }

    public Set<String> getFiredRules() {
        return firedRules;
    }

    // Métodos vacíos que no necesitamos, pero deben estar implementados
    @Override public void matchCreated(MatchCreatedEvent event) {}
    @Override public void matchCancelled(MatchCancelledEvent event) {}
    @Override public void agendaGroupPopped(AgendaGroupPoppedEvent event) {}
    @Override public void agendaGroupPushed(AgendaGroupPushedEvent event) {}
    @Override public void beforeMatchFired(BeforeMatchFiredEvent event) {}
    @Override public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {}
    @Override public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {}
    @Override public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {}
    @Override public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {}

}
