package com.paypal.butterfly.metrics.couchdb;

import com.paypal.butterfly.extensions.api.metrics.TransformationMetrics;
import com.paypal.butterfly.extensions.api.metrics.TransformationMetricsListener;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Configuration
public class CouchDbMetricsConfig {

    private static final Logger logger = LoggerFactory.getLogger(CouchDbMetricsConfig.class);

    @Bean
    public TransformationMetricsListener couchDbMetrics() {
        return new TransformationMetricsListener() {

            private CouchDbClient dbClient;

            @PostConstruct
            public void postConstruct() {
                dbClient = new CouchDbClient();
            }

            @Override
            public void notify(List<TransformationMetrics> metricsList) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Persisting transformation metrics in CouchDB");
                }

                dbClient.bulk(metricsList, false);

                if (logger.isDebugEnabled()) {
                    logger.debug("{} transformation metrics persisted in CouchDB", metricsList.size());
                }
            }

            @PreDestroy
            public void preDestroy() {
                dbClient.shutdown();
            }

        };
    }

}