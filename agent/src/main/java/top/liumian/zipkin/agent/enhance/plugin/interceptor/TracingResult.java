package top.liumian.zipkin.agent.enhance.plugin.interceptor;

import brave.Span;

/**
 * @author liumian  2022/8/25 23:55
 */
public class TracingResult {

    /**
     * 是否继续进行链路跟踪
     */
    private boolean continueTracing;


    /**
     * 如果continueTracing == true，那么span不为空
     */
    private Span span;

    public TracingResult(boolean continueTracing, Span span) {
        this.continueTracing = continueTracing;
        this.span = span;
    }

    public boolean isContinueTracing() {
        return continueTracing;
    }

    public void setContinueTracing(boolean continueTracing) {
        this.continueTracing = continueTracing;
    }

    public Span getSpan() {
        return span;
    }

    public void setSpan(Span span) {
        this.span = span;
    }
}
