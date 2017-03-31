package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.metrics.TransformationMetrics;
import com.paypal.butterfly.extensions.api.metrics.TransformationMetricsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Transformation metrics handler notifies any interested object about
 * the latest transformation metrics available
 *
 * @author facarvalho
 */
@Component
public class MetricsHandler implements TransformationListener {

    @Autowired
    private ApplicationContext applicationContext;

    private Collection<TransformationMetricsListener> listeners;

    @PostConstruct
    public void setupListeners() {
        Map<String, TransformationMetricsListener> beans = applicationContext.getBeansOfType(TransformationMetricsListener.class);
        listeners = beans.values();
    }

    @Override
    public void postTransformation(Transformation transformation, List<TransformationContextImpl> transformationContexts) {
        generateMetrics(transformation, transformationContexts);
    }

    @Override
    public void postTransformationAbort(Transformation transformation, List<TransformationContextImpl> transformationContexts) {
        generateMetrics(transformation, transformationContexts);
    }

    private void generateMetrics(Transformation transformation, List<TransformationContextImpl> transformationContexts) {
        List<TransformationMetrics> metricsList = new ArrayList<>();
        TransformationMetrics metrics;
        for (TransformationContextImpl transformationContext : transformationContexts) {
            metrics = new TransformationMetricsImpl(transformation, transformationContext);
            metricsList.add(metrics);
        }

        for (TransformationMetricsListener listener : listeners) {
            listener.notify(metricsList);
        }
    }

}
