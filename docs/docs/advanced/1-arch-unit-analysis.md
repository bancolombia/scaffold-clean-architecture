---
sidebar_position: 1
---

# ArchUnit Analysis

This document describes how we use ArchUnit to enforce architectural constraints in your project.

## Predefined Architecture Rules

### Rule_1.5: Reactive flows should use aws async clients

![Static Badge](https://img.shields.io/badge/kind-Reactive%20Programming-blue)

This rule try to enforce the usage of AsyncClient variants of AWS SDK, for example the usage of `kmsAsyncClient` instead
of `kmsClient` in reactive flows using the `Mono.fromFuture` method.

The rule is only generated in **reactive** projects, imperative projects do not include it.

#### Beans created at startup

Classes whose name ends with `Config` are excluded from the rule, so a bean that needs a synchronous client to be built
can use it there. This code runs once while the application starts, before any request is attended, so it does not
block the reactive flows. For example, reading a secret to build a bean:

```java
@Configuration
public class DbCredentialsConfig {

  public record DbCredentials(String user, String password) {}

  @Bean
  public DbCredentials dbCredentials(
      @Value("${aws.region}") String region,
      @Value("${db.user}") String user,
      @Value("${db.secret-name}") String secretName) {
    try (SecretsManagerClient client =
        SecretsManagerClient.builder().region(Region.of(region)).build()) {
      String password =
          client.getSecretValue(request -> request.secretId(secretName)).secretString();
      return new DbCredentials(user, password);
    }
  }
}
```

:::warning[The exclusion is by class name]
The name must end exactly with `Config`: `DbCredentialsConfig` is excluded, but `DbCredentialsConfiguration` is not.
The rule checks the class that uses the client, so if a `Config` class delegates the call to a helper class, the helper
is reported. Use the synchronous client only at startup; in any other flow keep the asynchronous one.
:::

#### Secrets read with the Secrets Manager adapter

The generated Secrets Manager adapter uses `GenericManagerAsync`, which is asynchronous. To build a bean with the secret
values, wait for the result once at startup:

```java
return secretManager.getSecret(secretName, SecretModel.class).toFuture().join();
```

:::note[Possible null warning in SonarQube]
Some SonarQube rules, such as organization-specific ones, may report that `join()` can return `null`. This is a false
positive for `getSecret` of the `aws-secrets-manager-async` library used by the scaffold: the returned `Mono` emits a
non-null value or fails (with a `SecretException` when the secret is missing, empty or not a string), it never completes
empty. If your quality profile reports it, mark it as a false positive.
:::

### Rule_2.2: Domain classes should not be named with technology suffixes

![Static Badge](https://img.shields.io/badge/kind-Clean%20Architecture-blue)

This rule is intended to avoid the technical names usage in domain entities. We avoid UserDTO name instead of simply
define as User.

you can use the `gradle.properties` file to set the forbidden suffixes, by default is:

```properties
arch.unit.forbiddenDomainSuffixes=dto,request,response
```

Note that plugin will treat `dto` suffix also like `Dto` and `DTO`

> ❌ No Compliant

```java
package co.com.bancolombia.model.userrequest;

public class UserRequest {
    private String name;
    private String email;
}
```

> ✅ Compliant

```java
package co.com.bancolombia.model.user;

public class User {
    private String name;
    private String email;
}
```

### Rule_2.4: Domain classes should not be named with technology names

![Static Badge](https://img.shields.io/badge/kind-Clean%20Architecture-blue)

This rule is intended to avoid the technical names usage in domain entities, gateways and use cases. We avoid
DynamoUserGateway name instead of simply define as UserGateway.

you can use the `gradle.properties` file to set the forbidden partial class names, by default is:

```properties
arch.unit.forbiddenDomainClassNames=rabbit,sqs,sns,ibm,dynamo,aws,mysql,postgres,redis,mongo,rsocket,r2dbc,http,kms,s3,graphql,kafka
```

Note that plugin will treat `rabbit` suffix also like `Rabbit` and `RABBIT`

> ❌ No Compliant

```java
package co.com.bancolombia.model.rabbitmessage;

public class RabbitMessage {
    private String contentType;
    private byte[] content;
}
```

> ✅ Compliant

```java
package co.com.bancolombia.model.message;

public class Message {
    private String contentType;
    private byte[] content;
}
```

### Rule_2.7: Domain classes should not have fields named with technology names

![Static Badge](https://img.shields.io/badge/kind-Clean%20Architecture-blue)

The same purpose of `Rule_2.4` but applied to domain model fields or use case fields.

you can use the `gradle.properties` file to set the forbidden field names, by default is the same that `Rule_2.4`, and
can be override with the same property.

> ❌ No Compliant

```java
package co.com.bancolombia.model.message;

public class Message {
    private String rabbitContentType;
    private byte[] content;
}
```

> ✅ Compliant

```java
package co.com.bancolombia.model.message;

public class Message {
    private String contentType;
    private byte[] content;
}
```

### Rule_2.5: UseCases should only have final attributes to avoid concurrency issues

![Static Badge](https://img.shields.io/badge/kind-Concurrency%20Safe%20Code-blue)

This rule is designed to avoid the shared resources in concurrency, all dependencies of a Use Case should be immutable,
and each context call should have their own resources.

> ❌ No compliant

```java
package co.com.bancolombia.usecase.notifyusers;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotifyUsersUseCase {
    private NotificationGateway notifier; // No Compliant
    private User user; // No Compliant


    public void notify(User user) {
        this.user = user;
        // some other business logic
        this.sendMessage()
    }

    private void sendMessage() {
        // send message througth class instance of user
        notifier.send("notification", user.getEmail())
    }
}
```

> ✅ Compliant

```java
package co.com.bancolombia.usecase.notifyusers;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotifyUsersUseCase {
    private final NotificationGateway notifier; // Compliant

    public void notify(User user) {
        // some other business logic
        this.sendMessage(user)
    }

    private void sendMessage(User user) {
        // send message througth input instance of user
        notifier.send("notification", user.getEmail())
    }
}
```

### Rule_2.7: Beans classes should only have final attributes (injection by constructor) to avoid concurrency issues

![Static Badge](https://img.shields.io/badge/kind-Concurrency%20Safe%20Code-blue)

This rule is designed to avoid the shared resources in concurrency, all dependencies of a Bean should be immutable, and
each context call should have their own resources. We promote the dependencies injection through constructor over the
@Value annotated fields injection.

> ❌ No Compliant

```java

@Component
public class MyService {
    @Value("${external-service.endpoint}")
    private String endpoint; // No Compliant

    public void someFunction(SomeObject domainObject) {
        // ... logic that uses endpoint
    }
}
```

> ✅ Compliant

```java

@Component
public class MyService {
    private final String endpoint; // Compliant

    public MyService(@Value("${external-service.endpoint}") String endpoint) {
        this.endpoint = endpoint;
    }

    public void someFunction(SomeObject domainObject) {
        // ... logic that uses endpoint
    }
}
```

## Skip Rules

If you want to skip Arch Unit rules validation, you can use the `gradle.properties` file to add
`arch.unit.ski=true`.

We recommend not to skip the rules, but if you need to do it, you can add the property to the `gradle.properties` file.

For example:

```properties title="gradle.properties"
package=co.com.bancolombia
systemProp.version=3.20.15
reactive=true
lombok=true
metrics=true
language=java
org.gradle.parallel=true
systemProp.sonar.gradle.skipCompile=true
arch.unit.skip=true # Skip Arch Unit rules validation
```