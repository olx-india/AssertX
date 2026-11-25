package com.olx.assertx.it.probe;

import java.sql.Connection;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import redis.clients.jedis.Jedis;

/**
 * GET /ready/{dependency} answers 200 once that dependency serves real traffic, 503 otherwise.
 * AssertX starts containers without waiting for their health checks, so scenarios poll this first.
 */
@RestController
public class ReadinessController {

    private final Deps deps;
    private final RedisController redis;
    private final SqlController sql;
    private final KafkaController kafka;
    private final RestClient http;

    public ReadinessController(Deps deps, RedisController redis, SqlController sql, KafkaController kafka) {
        this.deps = deps;
        this.redis = redis;
        this.sql = sql;
        this.kafka = kafka;
        this.http = Http.client(Duration.ofSeconds(3));
    }

    @GetMapping("/ready/{dependency}")
    public ResponseEntity<Map<String, String>> ready(@PathVariable String dependency) {
        Callable<?> check = switch (dependency) {
            case "redis" -> () -> {
                try (Jedis j = redis.jedis()) {
                    return j.ping();
                }
            };
            case "mysql", "postgresql" -> () -> {
                try (Connection c = sql.connect(dependency)) {
                    return c.createStatement().executeQuery("SELECT 1").next();
                }
            };
            case "kafka" -> kafka::topics;
            case "localstack" -> () -> get(deps.url(deps.localstack()) + "/_localstack/health");
            case "solr" -> () -> get(deps.url(deps.solr()) + "/solr/items/admin/ping?wt=json");
            case "opensearch" -> () -> get(deps.url(deps.opensearch()) + "/_cluster/health");
            case "user" -> () -> get(deps.url(deps.user()) + "/healthcheck");
            case "payment" -> () -> get(deps.url(deps.payment()) + "/healthcheck");
            case "echo" -> () -> get(deps.url(deps.echo()) + "/");
            default -> null;
        };
        if (check == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "unknown dependency " + dependency));
        }
        try {
            check.call();
            return ResponseEntity.ok(Map.of("dependency", dependency, "status", "UP"));
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of("dependency", dependency, "status", "DOWN",
                    "error", e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }

    private String get(String url) {
        return http.get().uri(url).retrieve().body(String.class);
    }
}
