package cn.solairelight.event;

/**
 * @author Joel Ou
 */
public interface Event<T> {

    void apply(EventContext<T> context);
}
