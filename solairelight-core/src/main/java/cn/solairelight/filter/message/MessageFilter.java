package cn.solairelight.filter.message;

import cn.solairelight.filter.Filter;

/**
 * @author Joel Ou
 */
public abstract class MessageFilter implements Filter<Object> {

    //0=in 1=out
    protected int inOrOut = 0;

    public MessageFilter(int inOrOut) {
        this.inOrOut = inOrOut;
    }
}
