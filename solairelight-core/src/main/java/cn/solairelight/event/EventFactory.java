package cn.solairelight.event;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @author Joel Ou
 */
@Component
public class EventFactory {
    private static Map<EventContext.EventType, EventTrigger> eventTriggers;

    public EventFactory(Map<EventContext.EventType, EventTrigger> eventTriggers){
        EventFactory.eventTriggers = eventTriggers;
    }

    public static EventTrigger getTrigger(EventContext.EventType eventType){
        return eventTriggers.get(eventType);
    }

    public static Set<Event<Object>> getEvent(EventContext.EventType eventType){
        return EventRepository.getEvents(eventType);
    }
}
