package com.olx.assertx.it.probe;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private final Deps deps;

    public RedisController(Deps deps) {
        this.deps = deps;
    }

    Jedis jedis() {
        return new Jedis(deps.host(), deps.redis(), 2000);
    }

    @PutMapping("/keys/{key}")
    public ResponseEntity<Map<String, String>> put(@PathVariable String key, @RequestBody Map<String, String> body) {
        try (Jedis jedis = jedis()) {
            jedis.set(key, body.get("value"));
        }
        return ResponseEntity.status(201).body(Map.of("key", key, "value", body.get("value")));
    }

    @GetMapping("/keys/{key}")
    public ResponseEntity<Map<String, String>> get(@PathVariable String key) {
        try (Jedis jedis = jedis()) {
            String value = jedis.get(key);
            return value == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(Map.of("key", key, "value", value));
        }
    }

    @DeleteMapping("/keys/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        try (Jedis jedis = jedis()) {
            return jedis.del(key) > 0 ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        }
    }

    /** Reads a server setting, to prove the mounted redis.conf (configFilePath) was applied. */
    @GetMapping("/config/{name}")
    public Map<String, String> config(@PathVariable String name) {
        try (Jedis jedis = jedis()) {
            return Map.of("name", name, "value", jedis.configGet(name).getOrDefault(name, ""));
        }
    }
}
