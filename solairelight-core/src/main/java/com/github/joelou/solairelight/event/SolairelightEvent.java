package com.github.joelou.solairelight.event;

/**
 * @author Joel Ou
 */
public interface SolairelightEvent<T> {

    void execute(EventContext<T> context);
}
