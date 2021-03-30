### HAProxy tutorial:

- HAProxy port: **24020**
- HAProxy configuration location: **/etc/haproxy/haproxy.cfg**
- HAProxy statistics URL: **<env_url>:9999/haproxy_stats**

HAProxy works like a gateway and a proxy for our backend services including keycloak,
it's started with **sudo systemctl start haproxy** and stopped like a normal daemon,
it should be launched on server startup automatically, before any changes to the configuration
the service should be stopped and then restarted.

#### New backend service deployment tutorial:

After deploying the service on the environment and starting it, it must be added to
the HAProxy configuration file as a new backend service, in order to do that you must:
1. Test if the .jar builds, run tests locally.
2. sudo systemctl stop haproxy
3. Check if haproxy is stopped with sudo systemctl status haproxy
4. Add the backend to the configuration file (tutorial below).
5. sudo systemctl start haproxy
6. Check if the service shows up on the HAProxy statistics page.

#### HAProxy configuration tutorial:

Overall adding a new backend looks like this:

```
backend <service-name>
    balance roundrobin      # set this to roundrobin always because we only have one server anyways
    server server-1 localhost:<service-port> check      # this is a tcp socket connection check
    option httpchk GET localhost:<service-port>/<service-name>/health       # this is an optional HTTP healthcheck
    
(... some stuff ...)

frontend front
    (... some stuff and other backend services ...)
    use_backend <service-name> if { path_beg /<service-name> }
```

- service-name is the name of the new service
- service-port is the port of the service, set in app properties, should be unique to the rest, something like 2 + port of the previous service
The optional HTTP healthcheck should be included only when the appropriate endpoint is set up in the service, it should look something like this in code:
```java
@GetMapping("/health")
public Response health() {
    return Response.ok();
}
```



        
