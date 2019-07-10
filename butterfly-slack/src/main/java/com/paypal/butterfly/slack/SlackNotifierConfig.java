package com.paypal.butterfly.slack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.paypal.butterfly.api.TransformationListener;
import com.paypal.butterfly.api.TransformationRequest;
import com.paypal.butterfly.api.TransformationResult;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Observer that notifies the transformation requester
 * via Slack when a transformation finishes.
 *
 * @author edhwang
 */
@Configuration
public class SlackNotifierConfig {
    private static final Logger logger = LoggerFactory.getLogger(SlackNotifierConfig.class);

    @Bean
    public TransformationListener slackNotification() {
        return new TransformationListener() {

            private String slackAuthenticationToken;
            private WebTarget slackTarget;
            private String postSuccessJson;
            private String postAbortJson;
            private boolean active = false;
            private Map<String, String> slackEmailCache;

            @PostConstruct
            public void postConstruct() {
                try {
                    slackAuthenticationToken = System.getenv("SLACK_AUTH_TOKEN");
                    if (slackAuthenticationToken == null) {
                        logger.error("Unable to locate Slack authentication token to send notifications");
                        return;
                    }
                    try (InputStream successStream = this.getClass().getResourceAsStream("/postSuccess.json");
                         InputStream abortStream = this.getClass().getResourceAsStream("/postAbort.json")) {
                        postSuccessJson = IOUtils.toString(successStream, StandardCharsets.UTF_8);
                        postAbortJson = IOUtils.toString(abortStream, StandardCharsets.UTF_8);
                    }
                    Client client = ClientBuilder.newClient();
                    slackTarget = client.target("https://www.slack.com/api/");
                    slackEmailCache = Collections.synchronizedMap(new LRUMap(1000));
                    active = true;
                } catch (IOException e) {
                    logger.error("Unable to read the Slack message template file from the path", e);
                }
            }

            @Override
            public void postTransformation(TransformationRequest transformationRequest, TransformationResult transformationResult) {
                postNotification(transformationRequest, postSuccessJson);
            }

            @Override
            public void postTransformationAbort(TransformationRequest transformationRequest, TransformationResult transformationResult) {
                String errorMessage = transformationResult.getAbortDetails().getAbortMessage();
                postNotification(transformationRequest, String.format(postAbortJson, "%s", errorMessage, "%s", "%s"));
            }

            private void postNotification(TransformationRequest transformationRequest, String jsonMessage) {
                if (!active) {
                    return;
                }
                Properties properties = transformationRequest.getConfiguration().getProperties();
                String detailsLink = properties.getProperty("slack.detailsLink");
                String contactLink = properties.getProperty("slack.contactLink");
                String usersNotificationProperty = properties.getProperty("usersNotification");
                if (usersNotificationProperty == null) {
                    logger.info("No Slack users have been selected for transformation notifications");
                } else {
                    if (detailsLink == null) {
                        logger.info("No transformation details link has been provided");
                    }
                    if (contactLink == null) {
                        logger.info("No transformation contact link has been provided");
                    }
                    String[] slackUserEmails = usersNotificationProperty.split(",");
                    for (String email : slackUserEmails) {
                        try {
                            if (!slackEmailCache.containsKey(email)) {
                                String slackUserId = getSlackUserId(slackAuthenticationToken, email);
                                slackEmailCache.put(email, slackUserId);
                            }
                            postSlackMessage(slackAuthenticationToken, jsonMessage, email, detailsLink, contactLink);
                        } catch (RuntimeException e) {
                            logger.warn("An exception happened when sending the Slack message", e);
                        }
                    }
                }
            }

            /**
             * GET slack user IDs from PayPal email using the users.lookupByEmail web api call
             */
            private String getSlackUserId(String token, String email) throws ProcessingException {
                String slackUserId;
                JsonObject slackResponse = new JsonParser().parse(slackTarget
                        .path("users.lookupByEmail")
                        .queryParam("email", email)
                        .request(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .get(String.class)).getAsJsonObject();
                if (slackResponse.get("ok").getAsBoolean()) {
                    slackUserId = slackResponse.getAsJsonObject("user").get("id").getAsString();
                } else {
                    throw new RuntimeException("An error happened when retrieving Slack user id: " + slackResponse.get("error").getAsString());
                }
                return slackUserId;
            }

            /**
             * POST slack message to all slackIDs using the chat.postMessage web api call
             */
            private void postSlackMessage(String token, String jsonFileString, String email, String detailsLink, String contactLink) throws ProcessingException {
                String jsonFileStringFormat = String.format(jsonFileString, slackEmailCache.get(email), detailsLink, contactLink);
                JsonObject slackResponse = new JsonParser().parse(slackTarget
                        .path("chat.postMessage")
                        .request(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .header("Content-type", "application/json")
                        .post(Entity.entity(jsonFileStringFormat, MediaType.APPLICATION_JSON), String.class))
                        .getAsJsonObject();
                if (slackResponse != null && !slackResponse.get("ok").getAsBoolean()) {
                    if (slackResponse.get("error").getAsString().equals("channel_not_found")) {
                        slackEmailCache.remove(email);
                        logger.warn("The Slack user id in the cache is invalid");
                    } else {
                        logger.warn("An exception happened when posting the notification to Slack " + slackResponse.get("error").getAsString());
                    }
                } else {
                    logger.info("Slack notification has successfully be sent");
                }
            }
        };
    }
}
