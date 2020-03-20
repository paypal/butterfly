package com.paypal.butterfly.core;

import com.paypal.butterfly.api.TransformationListener;
import com.paypal.butterfly.api.TransformationRequest;
import com.paypal.butterfly.api.TransformationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;

/**
 * The transformation engine in charge of
 * applying transformations
 *
 * @author facarvalho
 */
@Component
class TransformationEngine {

    private Collection<TransformationListener> transformationListeners;

    @Autowired
    private ApplicationContext applicationContext;

    private ManualInstructionsHandler manualInstructionsHandler;

    private TransformationValidator validator;

    // One thread per core is not enough because most Butterfly transformations are IO heavy
    private static final int MIN_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAX_THREAD_POOL_SIZE = MIN_THREAD_POOL_SIZE * 2;

    private ExecutorService executor = new ThreadPoolExecutor(MIN_THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    @PostConstruct
    void setupListeners() {
        Map<String, TransformationListener> beans = applicationContext.getBeansOfType(TransformationListener.class);
        transformationListeners = beans.values();
        if (transformationListeners == null) {
            transformationListeners = Collections.emptyList();
        }

        validator = applicationContext.getBean(TransformationValidator.class);
        manualInstructionsHandler = applicationContext.getBean(ManualInstructionsHandler.class);
    }

    /**
     * Perform an application transformation based on the specified {@link TransformationRequest}
     * object
     *
     * @param transformationRequest the transformationRequest object
     * @return a {@link CompletableFuture} object referring to the result after performing this transformation request
     */
    CompletableFuture<TransformationResult> perform(TransformationRequest transformationRequest) {
        return CompletableFuture.supplyAsync(
                Transformer.createTransformer(transformationRequest, transformationListeners, manualInstructionsHandler, validator),
                executor
        );
    }

    @PreDestroy
    void shutdownExecutor() {
        executor.shutdown();
    }

}
