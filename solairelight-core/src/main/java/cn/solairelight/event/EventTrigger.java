package cn.solairelight.event;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joel Ou
 */
public class EventTrigger {

    private final EventContext.EventType eventType;

    private EventTrigger(EventContext.EventType eventType){
        this.eventType = eventType;
    }

    public static EventTrigger create(EventContext.EventType eventType) {
        return new EventTrigger(eventType);
    }

    public <T> Mono<Object> call(T argument){
        return Mono.create(sink->{
            Set<SolairelightEvent<?>> events = EventRepository.getEvents(this.eventType);
            AtomicInteger latch = new AtomicInteger(events.size());
            for (SolairelightEvent event : events) {
                SolairelightEventType eventType = EventRepository.getEventType(event.getClass());
                EventContext<Object> context = EventContext
                        .create()
                        .setEventType(eventType!=null?eventType.value():null)
                        .setTrigger(this.eventType.name())
                        .setArgument(argument);
                //async executing
                EventThreadPool.execute(()-> {
                    event.execute(context);
                    if(latch.decrementAndGet() == 0){
                        sink.success(argument);
                    }
                });
            }
        });
    }
}
