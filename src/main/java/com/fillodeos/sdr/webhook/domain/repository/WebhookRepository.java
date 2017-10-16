package com.fillodeos.sdr.webhook.domain.repository;

import com.fillodeos.sdr.webhook.domain.Webhook;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WebhookRepository extends PagingAndSortingRepository<Webhook, Long> {
    List<Webhook> findByEntityNameAndEventType(@Param("entityName") String entityName, @Param("eventType") String eventType);
}
