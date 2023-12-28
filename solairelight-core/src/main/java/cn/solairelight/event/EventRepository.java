package cn.solairelight.event;

import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author Joel Ou
 */
public class EventRepository {

    private static final Map<EventContext.EventType, Set<SolairelightEvent<?>>> events = new HashMap<>();

    public static void init(Set<SolairelightEvent<?>> events){
        events.forEach(event->{
            EventType eventType = null;
            for (Class<?> anInterface : event.getClass().getInterfaces()) {
                eventType = anInterface.getAnnotation(EventType.class);
            }
            if(eventType == null){
                return;
            }
            EventRepository.events.computeIfAbsent(eventType.value(), k->new LinkedHashSet<>()).add(event);
        });
    }

    public static Set<SolairelightEvent<?>> getEvents(EventContext.EventType eventType){
        return events.getOrDefault(eventType, Collections.emptySet());
    }
}
