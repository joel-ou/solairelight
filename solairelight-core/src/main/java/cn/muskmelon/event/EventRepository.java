package cn.muskmelon.event;

import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author Joel Ou
 */
@Repository
public class EventRepository {

    private static Map<EventContext.EventType, Set<Event<Object>>> events = new HashMap<>();

    public EventRepository(Set<Event> events){
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

    public static Set<Event<Object>> getEvents(EventContext.EventType eventType){
        return events.getOrDefault(eventType, Collections.emptySet());
    }
}
