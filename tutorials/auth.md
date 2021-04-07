### Authentication and authorization tutorials:

#### Authentication:

Endpoints:
- http://52.142.201.18:24020/auth/realms/sms/protocol/openid-connect/auth
- http://52.142.201.18:24020/auth/realms/sms/protocol/openid-connect/token

In order to obtain the token a POST request must be made to the /token endpoint,
the exact format can be looked up on google, it's an encoded information about the client, 
the username and the password.

#### Authorization:

Properties to include in the `application.properties` files:
```properties
keycloak.auth-server-url=http://52.142.201.18:24020/auth
keycloak.realm=sms
keycloak.resource=frontend
keycloak.public-client=true
```

Properties for role validation:
```properties
keycloak.security-constraints[0].auth-roles[0]=<role>
keycloak.security-constraints[0].security-collections[0].patterns=<url>
```

Replace `role` and `url` with the values you need, so for example if `url` is /books/* and `role` is STUDENT
then only students would be able to access endpoints beginning with /books.

Entries to add to `pom.xml`:

(the first one should be in the `<dependencies>` section obviously)
```xml
<dependency>
    <groupId>org.keycloak</groupId>
    <artifactId>keycloak-spring-boot-starter</artifactId>
</dependency>
```

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.keycloak.bom</groupId>
            <artifactId>keycloak-adapter-bom</artifactId>
            <version>11.0.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### User context injection:

Injecting user context into a spring bean:

```java
public class MyClass {

    ... code ...

    @Autowired
    UserContext userContext;
    
    ... more code ...
}
```

Using the user context:

```java
@GetMapping("/test")
public String test() {
    return "ID: " + userContext.getUserId() + "\n"
        + "Username: " + userContext.getUserName() + "\n"
        + "Roles: " + userContext.getRoles() + "\n"
        + "Bearer token: " + userContext.getToken() + "\n";
}
```