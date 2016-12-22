package com.paypal.butterfly.metrics.couchdb;

import com.paypal.butterfly.extensions.api.metrics.TransformationMetrics;
import com.paypal.butterfly.extensions.api.metrics.TransformationMetricsListener;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
@Configuration
public class CouchDbMetricsConfig {

    private static final Logger logger = LoggerFactory.getLogger(CouchDbMetricsConfig.class);

    @Bean
    public TransformationMetricsListener couchDbMetrics() {
        return new TransformationMetricsListener() {

            private CouchDbClient dbClient;
            private Exception ex;

            @PostConstruct
            public void postConstruct() {
                try {
                    dbClient = new CouchDbClient();
                } catch(Exception ex) {
                    this.ex = ex;
                }
            }

            @Override
            public void notify(List<TransformationMetrics> metricsList) {
                if (dbClient == null) {
                    if (logger.isDebugEnabled()) {
                        logger.warn("Exception when creating Couch DB client, metrics will not be persisted in Couch DB. Double check Couch DB configuration file, server and your network.", ex);
                    }
                    return;
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("Persisting transformation metrics in CouchDB");
                }

                try {
                    List<Response> responses = dbClient.bulk(metricsList, false);
                    for (Response response : responses) {
                        logger.info("Metrics record persisted in Couch DB with id {}", response.getId());
                    }
                } catch (CouchDbException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.warn("Exception when persisting metrics in Couch DB. Double check Couch DB configuration file, server and your network.", ex);
                    }
                }
            }

            @PreDestroy
            public void preDestroy() {
                if (dbClient != null) {
                    dbClient.shutdown();
                }
            }

        };
    }

}