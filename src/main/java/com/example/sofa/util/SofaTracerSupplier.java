package com.example.sofa.util;

import com.alipay.common.tracer.core.context.trace.SofaTraceContext;
import com.alipay.common.tracer.core.extensions.SpanExtensionFactory;
import com.alipay.common.tracer.core.holder.SofaTraceContextHolder;
import com.alipay.common.tracer.core.span.SofaTracerSpan;

import java.util.function.Supplier;

/**
 * 在线程之间传递span的supplier
 * @param <T>
 */
public class SofaTracerSupplier<T> implements Supplier<T> {

    private final long tid = Thread.currentThread().getId();
    private Supplier<T> wrappedSupplier;
    private SofaTraceContext traceContext;
    private SofaTracerSpan currentSpan;

    public SofaTracerSupplier(Supplier<T> supplier) {
        this(supplier, SofaTraceContextHolder.getSofaTraceContext());
    }

    public SofaTracerSupplier(Supplier<T> supplier, SofaTraceContext traceContext) {
        this.initSupplier(supplier, traceContext);
    }

    private void initSupplier(Supplier<T> supplier, SofaTraceContext traceContext) {
        this.wrappedSupplier = supplier;
        this.traceContext = traceContext;
        if (!traceContext.isEmpty()) {
            this.currentSpan = traceContext.getCurrentSpan();
        } else {
            this.currentSpan = null;
        }
    }

    @Override
    public T get() {
        if (Thread.currentThread().getId() != tid) {
            if (currentSpan != null) {
                traceContext.push(currentSpan);
                SpanExtensionFactory.logStartedSpan(currentSpan);
            }
        }
        try {
            return wrappedSupplier.get();
        } finally {
            if (Thread.currentThread().getId() != tid) {
                if (currentSpan != null) {
                    traceContext.pop();
                }
            }
        }
    }
}
