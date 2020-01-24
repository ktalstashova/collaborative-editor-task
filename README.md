# Collaborative editor
Java-Implementation of Collaborative Editor with Spring Boot and WebSocket usage. This editor uses an operation-based conflict-free replicated data type ([CRDT](https://en.wikipedia.org/wiki/Conflict-free_replicated_data_type)). It means that an editor propagates a state by transmitting only the update operations while all operations are commutative and idempotent.
The implementation includes a client and server sides.    
### Technology stack
* UI (Javascript, SockJS(WebSocket) jQuery, collections.js)
* Backend (SpringBoot, WebSocket, Junit, Mockito)

### Important Notes
The application client side was tested for a desktop version of Google Chrome Version 79.0.3945.130.

## Getting Started

### How to run
```shell script
gradlew bootRun
```

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.3.0.M1/gradle-plugin/reference/html/)
* [WebSocket](https://docs.spring.io/spring-boot/docs/2.3.0.M1/reference/htmlsingle/#boot-features-websockets)

### Guides
The following guides illustrate how to use some features concretely:

* [Using WebSocket to build an interactive web application](https://spring.io/guides/gs/messaging-stomp-websocket/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

