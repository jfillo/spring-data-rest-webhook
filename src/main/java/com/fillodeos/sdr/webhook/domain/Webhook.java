package com.fillodeos.sdr.webhook.domain;

import com.fillodeos.sdr.webhook.listener.jpa.WebhookEntityListener;

import javax.persistence.*;
import java.util.Set;

@Entity
@EntityListeners(WebhookEntityListener.class)
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String url;

    private String eventType;

    private String entityName;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> propertyList;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Set<String> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(Set<String> propertyList) {
        this.propertyList = propertyList;
    }

    @Override
    public String toString() {
        return "Webhook{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", entityName='" + entityName + '\'' +
                ", propertyList=" + propertyList +
                '}';
    }
}
