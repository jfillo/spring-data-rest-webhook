package com.fillodeos.sdr.webhook.listener.hibernate;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class EventListenerRegistryAdapter {

    @Autowired
    private HibernateEntityManagerFactory entityManagerFactory;

    @Autowired(required = false)
    private List<PreInsertEventListener> preInsertEventListeners;

    @Autowired(required = false)
    private List<PostInsertEventListener> postInsertEventListeners;

    @Autowired(required = false)
    private List<PreUpdateEventListener> preUpdateEventListeners;

    @Autowired(required = false)
    private List<PostUpdateEventListener> postUpdateEventListeners;

    @Autowired(required = false)
    private List<PreDeleteEventListener> preDeleteEventListeners;

    @Autowired(required = false)
    private List<PostDeleteEventListener> postDeleteEventListeners;

    @PostConstruct
    public void registerListeners() {
        // TODO: This method of getting a reference to the SessionFactory (and thereforce the ServiceRegistry) is Deprecated. Find out the right Hibernate 5.2 way to do this.
        EventListenerRegistry registry = entityManagerFactory.getSessionFactory().getServiceRegistry().getService(EventListenerRegistry.class);
        if (preInsertEventListeners != null) {
            registry.appendListeners(EventType.PRE_INSERT, preInsertEventListeners.toArray(new PreInsertEventListener[preInsertEventListeners.size()]));
        }
        if (postInsertEventListeners != null) {
            registry.appendListeners(EventType.POST_INSERT, postInsertEventListeners.toArray(new PostInsertEventListener[postInsertEventListeners.size()]));
        }
        if (preUpdateEventListeners != null) {
            registry.appendListeners(EventType.PRE_UPDATE, preUpdateEventListeners.toArray(new PreUpdateEventListener[preUpdateEventListeners.size()]));
        }
        if (postUpdateEventListeners != null) {
            registry.appendListeners(EventType.POST_UPDATE, postUpdateEventListeners.toArray(new PostUpdateEventListener[postUpdateEventListeners.size()]));
        }
        if (preDeleteEventListeners != null) {
            registry.appendListeners(EventType.PRE_DELETE, preDeleteEventListeners.toArray(new PreDeleteEventListener[preDeleteEventListeners.size()]));
        }
        if (postDeleteEventListeners != null) {
            registry.appendListeners(EventType.POST_DELETE, postDeleteEventListeners.toArray(new PostDeleteEventListener[postDeleteEventListeners.size()]));
        }
    }
}
