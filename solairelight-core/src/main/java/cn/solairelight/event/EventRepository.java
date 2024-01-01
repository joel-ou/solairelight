package cn.solairelight.event;

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

    public static Set<SolairelightEvent<?>> getEvents(EventContext.EventType eventType){
        Set<SolairelightEvent<?>> specificEvents = events.getOrDefault(eventType, Collections.emptySet());
        specificEvents.addAll(events.getOrDefault(EventContext.EventType.GLOBAL, Collections.emptySet()));
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
}
