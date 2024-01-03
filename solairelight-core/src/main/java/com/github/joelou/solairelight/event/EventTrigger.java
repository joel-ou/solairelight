package com.github.joelou.solairelight.event;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joel Ou
 */
public class EventTrigger {

    private final TriggerAction triggerAction;

    private EventTrigger(TriggerAction triggerAction){
        this.triggerAction = triggerAction;
    }

    public static EventTrigger create(TriggerAction triggerAction) {
        return new EventTrigger(triggerAction);
    }

    public <T> Mono<Object> call(T argument){
        return Mono.create(sink->{
            Set<SolairelightEvent<?>> events = EventRepository.getEvents(this.triggerAction);
            AtomicInteger latch = new AtomicInteger(events.size());
            for (SolairelightEvent event : events) {
                SolairelightEventType eventType = EventRepository.getEventType(event.getClass());
                EventContext<Object> context = EventContext
                        .create()
                        .setEventType(eventType!=null?eventType.value():null)
                        .setTrigger(this.triggerAction)
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

    public static enum TriggerAction {
        SESSION_CONNECTED,
        SESSION_DISCONNECTED,
        MESSAGE;
    }
}
