package com.fillodeos.sdr.webhook.hook;

import com.fillodeos.sdr.webhook.domain.Webhook;
import com.fillodeos.sdr.webhook.domain.repository.WebhookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebhookManager {

    public WebhookRepository webhookRepository;

    @Autowired
    public WebhookManager(WebhookRepository webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    @Cacheable("webhooks")
    public List<Webhook> retrieveWebhooksByEntityNameAndEventType(String entityName, String eventType) {
        return webhookRepository.findByEntityNameAndEventType(entityName, eventType);
    }
}
