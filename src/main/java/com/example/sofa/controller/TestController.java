package com.example.sofa.controller;

import com.alipay.common.tracer.core.async.SofaTracerRunnable;
import com.alipay.common.tracer.core.async.TracedExecutorService;
import com.alipay.common.tracer.core.holder.SofaTraceContextHolder;
import com.example.sofa.util.SofaTracerSupplier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 2, 3, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadPoolExecutor.AbortPolicy());
        TracedExecutorService tracedExecutorService = new TracedExecutorService(threadPoolExecutor);
        System.out.println(SofaTraceContextHolder.getSofaTraceContext().getCurrentSpan().getSofaTracerSpanContext().getTraceId());
        tracedExecutorService.execute(() -> System.out.println(SofaTraceContextHolder.getSofaTraceContext().getCurrentSpan().getSofaTracerSpanContext().getTraceId()));
        CompletableFuture.supplyAsync(new SofaTracerSupplier<>(() -> {
            System.out.println(SofaTraceContextHolder.getSofaTraceContext().getCurrentSpan().getSofaTracerSpanContext().getTraceId());
            return null;
        }));
        return "test";
    }


}
