# Mocks

The default Docker image of every built-in mock is pinned in
`src/main/resources/config.yml` (for example `redis:7.4`, `mysql:8.4`).
Override any of them from your project's `api-testing.yml` by setting
the service's `image` key, as shown in
[Getting Started](getting-started.md).

AssertX starts every dependency declared in `api-testing.yml` as a Docker container before your app boots. See the [configuration reference](getting-started.md#configuration) for all options.

## Health Check
For all the global or user specified mocks, there is a `/healthcheck` endpoint available by default.

**For example :-**

Suppose, the external services container is running on port `31270` and user service mock is running on port `31721`.

Then, the health check using `curl` can be done as follows :
1. **External Services Container** -
   `curl -X GET http://0.0.0.0:31270/healthcheck`
2. **User Service Mock** - 
   `curl -X GET http://0.0.0.0:31271/healthcheck`

## How to check service or mock ports
1. If toxiproxy is enabled, the value corresponding to the `listen` field for a mock in `it/mocks/toxiproxy/config.json` is its port.
   
   For example :-
   
   ```
   {
      "name": "user-service",
      "listen": "0.0.0.0:31270",
      "upstream": "0.0.0.0:31274"
   }
   ```
   In the above config, the port for user service is `31270`.

2. If toxiproxy is disabled, you can run `docker ps` for getting the container ports.
   
   a.) All global or user specified mocks will have the port same as the external service container port
   
   b.) For other dependencies (like redis, mysql), you can simply pick their corresponding container ports.

## How to write Custom Mocks
1. Define a model class for your custom mock extending `BaseService`.
   
   For example :-

   ```
   @EqualsAndHashCode(callSuper = true)
   @JsonInclude(JsonInclude.Include.NON_NULL)
   @JsonPropertyOrder({
      "image",
      "container_name",
      "ports",
      "environment",
      "entrypoint",
      "healthcheck",
      "networks"
   })
   @Getter
   @Setter
   public class Solr extends BaseService {
      String image;
      List<String> entrypoint;
      
      @Builder
      public Solr(String containerName,
                  List<String> ports,
                  List<String> environment,
                  Networks networks,
                  Healthcheck healthcheck,
                  String image,
                  List<String> entrypoint) {
         super(containerName, ports, environment, networks, healthcheck);
         this.image = image;
         this.entrypoint = entrypoint;
      }
   }
   ```

2. Define a custom mock class implementing `BaseCustomMock`.
   
   For example :-
   
   ```
   public class SolrMock implements BaseCustomMock {
   
     @Override
     public boolean isEnabled() {                                                   // Defines whether mock is enabled or not         
       return true;
     }
   
     @Override
     public BaseService map(int port) {                                             // Return the object of your custom mock defined above 
       return Solr.builder()
         .image("solr:9.7")
         .containerName(getServiceName())
         .ports(Collections.singletonList(port + ":" + port))
         .environment(List.of())
         .networks(Networks.builder().build())
         .entrypoint(List.of("sh", "-c",
           "precreate-core my_core; solr-foreground"))
         .healthcheck(Healthcheck.builder()
           .test(List.of("CMD-SHELL",
             "curl -s -A 'healthcheck'  http://localhost:" + port + "/solr/my_core/admin/ping?wt=json | grep -q '\"status\":\"OK\"'"))
           .interval("10s")
           .timeout("5s")
           .retries(3)
           .build())
         .build();
     }
   
     @Override
     public String getServiceName() {                                              // Return the name of your custom mock
       return "solr";
     }
   
     @Override
     public int getServicePort() {                                                 // Return the port of your custom mock
       return 8983;
     }
   }
   ```

3. Specify the fully qualified path of your custom mock class in `api-testing.yml` under `mocks.custom` section as mentioned in `Configuration` step.


## How to use jedis client while validation?
The following method is exposed in BaseStepDefinition class.
public Jedis getJedisClient() {...}

The method should be called to get the client in custom step definitions and we can perform the desired operations on redis 
For Ex. get(String key), set(String key, String value) etc.


