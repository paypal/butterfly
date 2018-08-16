package com.paypal.butterfly.persist.couchdb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.paypal.butterfly.api.TransformationListener;
import com.paypal.butterfly.api.TransformationRequest;
import com.paypal.butterfly.api.TransformationResult;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.io.IOException;

@SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
@Configuration
public class PersistCouchDbConfig {

    private static final Logger logger = LoggerFactory.getLogger(PersistCouchDbConfig.class);

    private static final GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().setPrettyPrinting().registerTypeAdapter(File.class, new TypeAdapter<File>() {
        @Override
        public void write(JsonWriter jsonWriter, File file) throws IOException {
            String fileAbsolutePath = (file == null ? null : file.getAbsolutePath());
            jsonWriter.value(fileAbsolutePath);
        }
        @Override
        public File read(JsonReader jsonReader) {
            throw new UnsupportedOperationException("There is no support for deserializing transformation result objects at the moment");
        }
    });

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TransformationListener persistCouchDb() {
        return new TransformationListener() {

            private CouchDbClient dbClient;
            private Exception ex;

            @PostConstruct
            public void postConstruct() {
                try {
                    dbClient = new CouchDbClient();
                    dbClient.setGsonBuilder(gsonBuilder);
                } catch(Exception ex) {
                    this.ex = ex;
                }
            }

            @Override
            public void postTransformation(TransformationRequest transformationRequest, TransformationResult transformationResult) {
                persist(transformationResult);
            }

            @Override
            public void postTransformationAbort(TransformationRequest transformationRequest, TransformationResult transformationResult) {
                persist(transformationResult);
            }

            private void persist(TransformationResult transformationResult) {
                if (dbClient == null) {
                    if (logger.isDebugEnabled()) {
                        logger.error("Exception when creating Couch DB client, transformation result will not be persisted in Couch DB. Double check Couch DB configuration file, server and your network.", ex);
                    } else {
                        logger.error("Exception when creating Couch DB client, transformation result will not be persisted in Couch DB. Run Butterfly in debug mode for further information.");
                    }
                    return;
                }
                logger.debug("Persisting transformation result in CouchDB");
                try {
                    Response response = dbClient.save(transformationResult);
                    logger.info("Transformation result persisted in Couch DB with id {}", response.getId());
                } catch (CouchDbException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.error("Exception when persisting transformation result in Couch DB. Double check Couch DB configuration file, server and your network.", ex);
                    } else {
                        logger.error("Exception when persisting transformation result in Couch DB. Run Butterfly in debug mode for further information.");
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