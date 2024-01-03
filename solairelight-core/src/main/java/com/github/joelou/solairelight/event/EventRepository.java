package com.github.joelou.solairelight.event;

import java.util.*;

/**
 * @author Joel Ou
 */
public class EventRepository {

    private static final Map<EventContext.EventType, Set<SolairelightEvent<?>>> events = new HashMap<>();

    public static void init(Set<SolairelightEvent<?>> events){
        events.forEach(event->{
            SolairelightEventType eventType = getEventType(event.getClass());
            if(eventType == null){
                return;
            }
            EventRepository.events.computeIfAbsent(eventType.value(), k->new LinkedHashSet<>()).add(event);
        });
    }

    public static Set<SolairelightEvent<?>> getEvents(EventTrigger.TriggerAction triggerAction){
        Set<SolairelightEvent<?>> specificEvents = events.getOrDefault(EventContext.EventType.GLOBAL,
                new HashSet<>());
        switch (triggerAction) {
            case SESSION_CONNECTED:
                padding(specificEvents, EventContext.EventType.SESSION_CONNECTED);
                break;
            case SESSION_DISCONNECTED:
                padding(specificEvents, EventContext.EventType.SESSION_DISCONNECTED);
                break;
            case MESSAGE:
                padding(specificEvents, EventContext.EventType.MESSAGE);
                break;
            default:
                break;
        }
        return specificEvents;
    }

    public static SolairelightEventType getEventType(Class<?> aClass){
        if(aClass == null)return null;
        for (Class<?> anInterface : aClass.getInterfaces()) {
            SolairelightEventType annotation = anInterface.getAnnotation(SolairelightEventType.class);
            if(annotation != null){
                return annotation;
            } else {
                return getEventType(anInterface);
            }
        }
        return null;
    }

    private static void padding(Set<SolairelightEvent<?>> specificEvents, EventContext.EventType eventType){
        specificEvents.addAll(events.getOrDefault(eventType, Collections.emptySet()));
    }
}
