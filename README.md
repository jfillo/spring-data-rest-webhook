## Spring Data Rest Webhooks via JPA Event Listeners

A sample Spring Boot application that attempts to
do a very basic webhook implementation for a Spring Data Rest project.

There are two implementations in this project. One uses strictly JPA
EntityListeners to trigger asynchronous to registered webhooks.
The other leverages Hibernate EventListeners.

### Getting Started
Head on over to the PersonWebhookTest for an integration test that proves out the
functionality of both implementations

You can also just run this Spring Boot application and mess around with it in PostMan
or whatever your favorite Rest client is.

By default this application is configurated to run on port 5000. Head on over to
the application.yml file to change that if you lie.


#### JPA EntityListener approach

I created the class WebhookEntityListener which implements
each of the JPA persistence listener methods. In these method is where
you'd want to retrieve your list of registered webhooks and notify any
of them who care about the change.

I only implemented the @PostUpdate method, but you could easily repeat the pattern
for the other entity listener methods.

Notes about this approach:
* The JPA EntityListener methods only knows about the
final state of the entity during updates. You only know that it changed,
not HOW it changed.
* I had to write some SMELLY code to allow Spring to get a handle on the EntityListener
as well as register it as an EntityListener on each of my Entity classes.

#### Hibernate EventListener approach

Hibernate has an EventListenerRegistry where you can register
classes to listen for Entity updates. Hibernate has a BUNCH more events you can register
with, but for this example I only implemented the ones that match the corresponded JPA
annotations. You could easily add others in EventListenerRegistryAdapter.

WebhookPostUpdateEventListener is a specific EventListener that does the 'heavy lifting'
of getting the list of registered webhooks and notifying them. If you wanted to add other
event listeners you would just expose them as Spring Beans that implement the interface
corresponding hibernate event.

Notes about this approach:
* The Hibernate EventListeners let you know which fields on an entity changed. This
allowed me to create a system where Webhooks could register to be notified only
when specific fields on an entity changed.
