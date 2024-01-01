package cn.solairelight.event;


import java.lang.annotation.*;

/**
 * @author Joel Ou
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SolairelightEventType {

    EventContext.EventType value() default EventContext.EventType.GLOBAL;
}
