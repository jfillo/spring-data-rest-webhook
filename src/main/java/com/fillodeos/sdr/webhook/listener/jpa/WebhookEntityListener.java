package com.fillodeos.sdr.webhook.listener.jpa;

import com.fillodeos.sdr.webhook.domain.Webhook;
import com.fillodeos.sdr.webhook.hook.WebhookManager;
import com.fillodeos.sdr.webhook.hook.WebhookProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.util.List;

@Component
public class WebhookEntityListener {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookEntityListener.class);

    @Autowired
    private WebhookManager webhookManager;

    @Autowired
    private WebhookProcessor webhookProcessor;

    @PrePersist
    public void prePersist(Object object) {
        LOG.info("Listening to pre persist for object:" + object);
    }

    @PostPersist
    public void postPersist(Object object) {
        LOG.info("Listening to post persist for object:" + object);
    }

    @PreUpdate
    public void preUpdate(Object object) {
        LOG.info("Listening to pre update for object:" + object);
    }

    @PostUpdate
    @Async
    public void postUpdate(Object object) {
        LOG.info("Listening to post update for object:" + object);
        // Entitys have to be annotated with @EventListeners and reference this class in that annotation, because of this
        // the usages of this class are not executed withing the handle of the Spring context. So now we have to use this funky
        // ass way of wiring in fields AS this method is being called. #sadface
        AutowireHelper.autowire(this);
        // Trying to just add @Transactional(Transactional.TxType.REQUIRES_NEW) to this method didn't work at all, it was just being ignored.
        // This wrapper is what ended up working.
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(int status) {
                super.afterCompletion(status);
                List<Webhook> hooks = webhookManager.retrieveWebhooksByEntityNameAndEventType(object.getClass().getSimpleName(), "post-update");
                hooks.stream().forEach(wh -> webhookProcessor.notifyWebhook(wh, object));
            }
        });

    }

    @PreRemove
    public void preRemove(Object object) {
        LOG.info("Listening to pre remove for object:" + object);
    }

    @PostRemove
    public void postRemove(Object object) {
        LOG.info("Listening to post remove for object:" + object);
    }
}
