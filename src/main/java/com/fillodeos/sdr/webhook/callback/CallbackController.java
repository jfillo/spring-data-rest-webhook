package com.fillodeos.sdr.webhook.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fillodeos.sdr.webhook.domain.Person;
import com.fillodeos.sdr.webhook.domain.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class CallbackController {

    private static final Logger LOG = LoggerFactory.getLogger(CallbackController.class);

    private CallbackTracker callbackTracker;
    private ObjectMapper objectMapper;

    @Autowired
    public CallbackController(CallbackTracker callbackTracker, ObjectMapper objectMapper) {
        this.callbackTracker = callbackTracker;
        this.objectMapper = objectMapper;
    }

    /**
     * Simple Webhook Recipient that just returns the object that was updated. We also increment which Object type we received for tracking purposes.
     */
    @RequestMapping(method = RequestMethod.POST, value = "callback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Object webhookCallbackHandler(HttpEntity<String> httpEntity) throws IOException {
        LOG.info("Webhook Callback Triggered, Headers:" + httpEntity.getHeaders() + " Object:" + httpEntity.getBody());
        return objectMapper.readValue(httpEntity.getBody(), getEntityClass(httpEntity.getHeaders().getFirst("webhook-entity-name")));
    }

    private Class getEntityClass(String entityName) {
        switch (entityName) {
            case "Person":
                callbackTracker.incrementPersonCallbackCount();
                return Person.class;
            case "Webhook":
                callbackTracker.incrementWebhookCallbackCount();
                return Webhook.class;
            default:
                return Object.class;
        }
    }
}
