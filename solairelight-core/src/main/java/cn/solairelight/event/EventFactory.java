package cn.solairelight.event;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Joel Ou
 */
public class EventFactory {
    private static Map<EventContext.EventType, EventTrigger> eventTriggers;

    private EventFactory() {
    }

    public static EventTrigger getTrigger(EventContext.EventType eventType){
        return eventTriggers.get(eventType);
    }

    public static void init(Set<SolairelightEvent<?>> events){
        //init triggers.
        EventContext.EventType[] eventTypes = EventContext.EventType.values();
        EventFactory.eventTriggers = Arrays.stream(eventTypes).collect(Collectors.toMap(t-> t, EventTrigger::create));

        //init event storage.
        EventRepository.init(events);
    }
}
