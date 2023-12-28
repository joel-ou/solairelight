package cn.solairelight.event;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
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
            Set<Event<Object>> events = EventRepository.getEvents(this.eventType);
            AtomicInteger latch = new AtomicInteger(events.size());
            for (Event<Object> event : events) {
                EventContext<Object> context = EventContext
                        .create()
                        .setEventType(this.eventType)
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
