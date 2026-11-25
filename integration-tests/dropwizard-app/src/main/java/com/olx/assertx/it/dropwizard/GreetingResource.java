package com.olx.assertx.it.dropwizard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import redis.clients.jedis.Jedis;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
public class GreetingResource {

    private final GreetingConfiguration config;
    private final ObjectMapper mapper;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

    public GreetingResource(GreetingConfiguration config, ObjectMapper mapper) {
        this.config = config;
        this.mapper = mapper;
    }

    @GET
    @Path("/greetings/{userId}")
    public Response greet(@PathParam("userId") String userId) throws Exception {
        HttpResponse<String> res = http.send(
                HttpRequest.newBuilder(URI.create(config.getUserServiceUrl() + "/api/v1/users/" + userId))
                        .timeout(Duration.ofSeconds(2)).build(),
                HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 404) {
            return Response.status(404).entity(Map.of("error", "user " + userId + " not found")).build();
        }
        JsonNode user = mapper.readTree(res.body());
        return Response.ok(Map.of("message", "Hello, " + user.path("name").asText() + "!")).build();
    }

    /** Increments a Redis counter, proving the custom Redis port reached the app. */
    @POST
    @Path("/visits/{page}")
    public Response visit(@PathParam("page") String page) {
        try (Jedis jedis = new Jedis(config.getRedisHost(), config.getRedisPort(), 2000)) {
            return Response.ok(Map.of("page", page, "visits", jedis.incr("visits:" + page))).build();
        }
    }

    @GET
    @Path("/ready")
    public Response ready() {
        try (Jedis jedis = new Jedis(config.getRedisHost(), config.getRedisPort(), 2000)) {
            jedis.ping();
            HttpResponse<String> res = http.send(
                    HttpRequest.newBuilder(URI.create(config.getUserServiceUrl() + "/healthcheck")).build(),
                    HttpResponse.BodyHandlers.ofString());
            return Response.status(res.statusCode()).entity(Map.of("status", "UP")).build();
        } catch (Exception e) {
            return Response.status(503).entity(Map.of("status", "DOWN", "error", String.valueOf(e))).build();
        }
    }
}
