package com.fillodeos.sdr.webhook;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fillodeos.sdr.webhook.callback.CallbackTracker;
import com.fillodeos.sdr.webhook.domain.Webhook;
import com.fillodeos.sdr.webhook.domain.repository.WebhookRepository;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DatabaseSetup("/dbunit/one_person.xml")
@DatabaseTearDown("/dbunit/empty.xml")
public class PersonWebhookTest extends AbstractWebIntegrationTest {

    @Autowired
    private WebhookRepository webhookRepository;

    @Autowired
    private CallbackTracker callbackTracker;

    @Before
    public void setup() {
        callbackTracker.resetPersonCallbackCount();
        callbackTracker.resetWebhookCallbackCount();
    }

    @Test
    public void patch_person_verify_webhook_without_property_change_match() throws Exception {
        // Register webhook. Callback url to this application
        Webhook webhook = new Webhook();
        webhook.setEntityName("Person");
        webhook.setEventType("post-update");
        webhook.setPropertyList(Collections.singleton("firstName"));
        webhook.setUrl(UriComponentsBuilder.fromHttpUrl("http://localhost").port(localServerPort).path("/callback").build().toUriString());
        webhookRepository.save(webhook);

        PersonPayload patchPerson = new PersonPayload();
        patchPerson.setLastName("Doe");
        this.mockMvc.perform(patch("/persons/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(patchPerson)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(RestMediaTypes.HAL_JSON))
                .andExpect(jsonPath("firstName", Matchers.is("John")))
                .andExpect(jsonPath("lastName", Matchers.is("Doe")));

        // Only the JPA listener that detects whole entity changes will trigger.
        // The Hibernate listener will not because only the last name changed and the hook is registered to listen to only first name changes
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertEquals(Integer.valueOf(1), callbackTracker.getPersonCallbackCount()));
    }

    @Test
    public void patch_person_verify_webhooks_with_property_change_match() throws Exception {
        // Register webhook. Callback url to this application
        Webhook webhook = new Webhook();
        webhook.setEntityName("Person");
        webhook.setEventType("post-update");
        webhook.setPropertyList(Collections.singleton("firstName"));
        webhook.setUrl(UriComponentsBuilder.fromHttpUrl("http://localhost").port(localServerPort).path("/callback").build().toUriString());
        webhookRepository.save(webhook);

        PersonPayload patchPerson = new PersonPayload();
        patchPerson.setFirstName("Jane");
        this.mockMvc.perform(patch("/persons/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(patchPerson)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(RestMediaTypes.HAL_JSON))
                .andExpect(jsonPath("firstName", Matchers.is("Jane")))
                .andExpect(jsonPath("lastName", Matchers.is("Smith")));

        // Both the JPA Listener that only detects whole entity changes will trigger as well as the Hibernate Listener that will detect specific property changes
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertEquals(Integer.valueOf(2), callbackTracker.getPersonCallbackCount()));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    class PersonPayload {
        private String firstName;
        private String lastName;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}
