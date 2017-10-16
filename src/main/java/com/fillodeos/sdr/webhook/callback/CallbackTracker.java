package com.fillodeos.sdr.webhook.callback;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *  This component keeps track of when webhook callbacks have occurred, we can use these when doing assertions in our Integration Tests
 */
@Component
public class CallbackTracker {

    private AtomicInteger personCallbackCount = new AtomicInteger();
    private AtomicInteger webhookCallbackCount = new AtomicInteger();

    public Integer getPersonCallbackCount() {
        return personCallbackCount.get();
    }

    public void incrementPersonCallbackCount() {
        personCallbackCount.incrementAndGet();
    }

    public void resetPersonCallbackCount() {
        personCallbackCount.set(0);
    }

    public Integer getWebhookCallbackCount() {
        return webhookCallbackCount.get();
    }

    public void incrementWebhookCallbackCount() {
        webhookCallbackCount.incrementAndGet();
    }

    public void resetWebhookCallbackCount() {
        webhookCallbackCount.set(0);
    }
}
