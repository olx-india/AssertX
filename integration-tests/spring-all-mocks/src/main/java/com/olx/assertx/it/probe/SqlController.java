package com.olx.assertx.it.probe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reads and writes the {@code users} table that each database's dataDir init script creates and seeds.
 * Connections are opened per request so the app boots even before the containers are ready.
 */
@RestController
public class SqlController {

    private final Deps deps;

    public SqlController(Deps deps) {
        this.deps = deps;
    }

    Connection connect(String db) throws SQLException {
        DriverManager.setLoginTimeout(5);
        return switch (db) {
            case "mysql" -> DriverManager.getConnection(
                    "jdbc:mysql://" + deps.host() + ":" + deps.mysql() + "/" + deps.mysqlDb()
                            + "?allowPublicKeyRetrieval=true&useSSL=false", "root", "1234");
            case "postgresql" -> DriverManager.getConnection(
                    "jdbc:postgresql://" + deps.host() + ":" + deps.postgresql() + "/test", "postgres", "postgres");
            default -> throw new IllegalArgumentException("unknown database " + db);
        };
    }

    @GetMapping("/{db:mysql|postgresql}/users/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String db, @PathVariable int id) throws SQLException {
        try (Connection c = connect(db);
             PreparedStatement ps = c.prepareStatement("SELECT id, name FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(Map.of("id", rs.getInt("id"), "name", rs.getString("name")));
            }
        }
    }

    @PostMapping("/{db:mysql|postgresql}/users")
    public ResponseEntity<Map<String, Object>> create(@PathVariable String db, @RequestBody Map<String, Object> body)
            throws SQLException {
        try (Connection c = connect(db);
             PreparedStatement ps = c.prepareStatement("INSERT INTO users (id, name) VALUES (?, ?)")) {
            ps.setInt(1, ((Number) body.get("id")).intValue());
            ps.setString(2, (String) body.get("name"));
            ps.executeUpdate();
        }
        return ResponseEntity.status(201).body(body);
    }
}
