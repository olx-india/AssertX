package com.example.greeting;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Greets a user by name. The name comes from a downstream "user-service",
 * which AssertX replaces with a mock during integration tests.
 */
@RestController
public class GreetingController {

    private final RestClient userService;

    public GreetingController(@Value("${clients.user-service.endpoint}") String userServiceEndpoint) {
        this.userService = RestClient.create(userServiceEndpoint);
    }

    @GetMapping("/api/v1/greetings/{userId}")
    public ResponseEntity<Map<String, String>> greet(@PathVariable String userId) {
        try {
            UserDto user = userService.get()
                    .uri("/api/v1/users/{userId}", userId)
                    .retrieve()
                    .body(UserDto.class);
            return ResponseEntity.ok(Map.of("message", "Hello, " + user.name() + "!"));
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "user " + userId + " not found"));
        }
    }

    record UserDto(String id, String name) {
    }
}
