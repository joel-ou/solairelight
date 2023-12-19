package cn.solairelight.event;

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

    public <T> void call(T argument){
        for (Event<Object> event : EventRepository.getEvents(this.eventType)) {
            EventContext<Object> context = EventContext
                    .builder()
                    .eventType(this.eventType)
                    .argument(argument)
                    .build();
            //async executing
            EventThreadPool.execute(()->event.apply(context));
        }
    }
}
