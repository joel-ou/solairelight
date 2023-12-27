package cn.solairelight.event;

/**
 * @author Joel Ou
 */
public interface Event<T> {

    void execute(EventContext<T> context);
}
