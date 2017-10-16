package com.fillodeos.sdr.webhook.hook;

import com.fillodeos.sdr.webhook.domain.Webhook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;

@Service
public class WebhookProcessor {

    private AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();

    public void notifyWebhook(Webhook webhook, Object entity) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("webhook-entity-name", webhook.getEntityName());
        httpHeaders.add("webhook-event-type", webhook.getEventType());
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        asyncRestTemplate.exchange(webhook.getUrl(), HttpMethod.POST, new HttpEntity<>(entity, httpHeaders), Object.class);
    }
}
