package com.paypal.butterfly.metrics.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paypal.butterfly.extensions.api.metrics.TransformationMetrics;
import com.paypal.butterfly.extensions.api.metrics.TransformationMetricsListener;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

// TODO Make it conditional, OFF by default
@Configuration
@SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
public class FileMetricsConfig {

    // TODO
    // A few improvements to do here in the future:
    //      - Make the metrics file name configurable
    //      - Make the metrics file location configurable
    //      - Make the metrics JSON content "pretty or compact" configurable
    //      - Add support to other formats, like YAML

    private static final Logger logger = LoggerFactory.getLogger(FileMetricsConfig.class);

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TransformationMetricsListener fileMetrics() {
        return new TransformationMetricsListener() {

            private Gson gson;

            @PostConstruct
            public void postConstruct() {
                GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
                gson = gsonBuilder.create();
            }

            @Override
            public void notify(List<TransformationMetrics> metricsList) {
                if (metricsList.size() == 0) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("There is no metrics to be printed to file");
                    }
                    return;
                }

                // First metric, used here to get some meta-data to be used
                // to name the metrics file
                TransformationMetrics fm = metricsList.get(0);

                String fileName = String.format("%s_%s.json", fm.getApplicationName(), fm.getTimestamp());
                File file = new File("metrics", fileName);

                try {
                    String metricsString = objectToJson(metricsList);
                    FileUtils.writeStringToFile(file, metricsString, Charset.defaultCharset());
                    logger.info("Metrics record persisted in file {}", file.getAbsolutePath());
                } catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.warn("It was not possible to write metrics to file", e);
                    }
                }
            }

            private String objectToJson(List<TransformationMetrics> metricsList) {
                return gson.toJson(metricsList);
            }

        };
    }

}