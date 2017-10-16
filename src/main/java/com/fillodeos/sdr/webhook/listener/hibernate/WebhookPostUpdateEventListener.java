package com.fillodeos.sdr.webhook.listener.hibernate;

import com.fillodeos.sdr.webhook.domain.Webhook;
import com.fillodeos.sdr.webhook.hook.WebhookManager;
import com.fillodeos.sdr.webhook.hook.WebhookProcessor;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class WebhookPostUpdateEventListener implements PostUpdateEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(WebhookPostUpdateEventListener.class);

    private WebhookManager webhookManager;
    private WebhookProcessor webhookProcessor;

    @Autowired
    public WebhookPostUpdateEventListener(WebhookManager webhookManager, WebhookProcessor webhookProcessor) {
        this.webhookManager = webhookManager;
        this.webhookProcessor = webhookProcessor;
    }

    @Override
    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW) // Make sure any JPA queries run in this method don't try and use the same session that triggered this event listener method
    public void onPostUpdate(PostUpdateEvent event) {
        LOG.info("hibernate post update on object: " + event.getEntity());
        List<Webhook> hooks = webhookManager.retrieveWebhooksByEntityNameAndEventType(event.getEntity().getClass().getSimpleName(), EventType.POST_UPDATE.eventName());
        // Webhooks that do not specify a property list will be informed of ANY update to the entity
        // Webhooks that specify a property list will only be informed if any of the properties defined have been updated.
        hooks.stream()
                .filter(wh -> wh.getPropertyList().isEmpty() || wh.getPropertyList().stream().anyMatch(propertyName -> isFieldModified(propertyName, event)))
                .forEach(wh -> webhookProcessor.notifyWebhook(wh, event.getEntity()));
    }

    private static boolean isFieldModified(String fieldName, PostUpdateEvent event) {
        Integer propertyIndex = event.getPersister().getEntityMetamodel().getPropertyIndexOrNull(fieldName);
        List<Integer> dirtyPropertyIndexes = Arrays.stream(event.getDirtyProperties()).boxed().collect(Collectors.toList());
        return dirtyPropertyIndexes.stream().anyMatch(dirtyPropertyIndex -> Objects.equals(dirtyPropertyIndex, propertyIndex));
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }
}
