package io.github.joelou.solairelight.event;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Joel Ou
 */
public class EventFactory {
    private static Map<EventTrigger.TriggerAction, EventTrigger> eventTriggers;

    private EventFactory() {
    }

    public static EventTrigger getTrigger(EventTrigger.TriggerAction triggerAction){
        return eventTriggers.get(triggerAction);
    }

    public static void init(Set<SolairelightEvent<?>> events){
        //init triggers.
        EventTrigger.TriggerAction[] triggerActions = EventTrigger.TriggerAction.values();
        EventFactory.eventTriggers = Arrays.stream(triggerActions).collect(Collectors.toMap(t-> t, EventTrigger::create));

        //init event storage.
        EventRepository.init(events);
    }
}
