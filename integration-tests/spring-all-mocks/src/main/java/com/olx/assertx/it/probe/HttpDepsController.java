package com.olx.assertx.it.probe;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Endpoints backed by HTTP dependencies: the JS and SOAP mocks served by the external-services container,
 * LocalStack S3, Solr, OpenSearch and the custom http-echo mock.
 */
@RestController
public class HttpDepsController {

    private static final Pattern SOAP_STATUS = Pattern.compile("<Status>([^<]*)</Status>");

    private final Deps deps;
    private final RestClient http;

    public HttpDepsController(Deps deps) {
        this.deps = deps;
        this.http = Http.client(Duration.ofSeconds(1));
    }

    private static boolean isTimeout(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof HttpTimeoutException || String.valueOf(t.getMessage()).contains("timed out")) {
                return true;
            }
        }
        return false;
    }

    /** Calls user-service; maps 404 to 404, a slow mock to 504 and an unreachable mock to 503. */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> user(@PathVariable String id) {
        try {
            JsonNode user = http.get().uri(deps.url(deps.user()) + "/api/v1/users/{id}", id).retrieve().body(JsonNode.class);
            return ResponseEntity.ok(user);
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", "user-service said " + e.getStatusCode().value()));
        } catch (ResourceAccessException e) {
            HttpStatus status = isTimeout(e) ? HttpStatus.GATEWAY_TIMEOUT : HttpStatus.SERVICE_UNAVAILABLE;
            return ResponseEntity.status(status).body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    /** Creates a user through the user-service mock's POST route. */
    @PostMapping("/users")
    public ResponseEntity<JsonNode> createUser(@RequestBody Map<String, Object> user) {
        JsonNode created = http.post().uri(deps.url(deps.user()) + "/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON).body(user).retrieve().body(JsonNode.class);
        return ResponseEntity.status(201).body(created);
    }

    /** Sends a SOAP envelope to the payment mock and extracts {@code <Status>} from the XML reply. */
    @PostMapping("/payments")
    public Map<String, String> pay(@RequestBody Map<String, String> body) {
        String envelope = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>"
                + "<Pay><OrderId>" + body.get("orderId") + "</OrderId></Pay></soap:Body></soap:Envelope>";
        String xml = http.post().uri(deps.url(deps.payment()) + "/payment/payment.asmx")
                .contentType(MediaType.TEXT_XML).header("SOAPAction", "Pay")
                .body(envelope).retrieve().body(String.class);
        Matcher m = SOAP_STATUS.matcher(String.valueOf(xml));
        return Map.of("orderId", body.get("orderId"), "status", m.find() ? m.group(1) : "UNKNOWN");
    }

    @GetMapping("/custom-echo")
    public Map<String, String> customEcho() {
        return Map.of("reply", String.valueOf(http.get().uri(deps.url(deps.echo()) + "/").retrieve().body(String.class)).trim());
    }

    // ---- LocalStack S3 (path-style, unsigned requests are accepted by LocalStack)

    @PutMapping("/s3/{bucket}/{key}")
    public ResponseEntity<Map<String, String>> s3Put(@PathVariable String bucket, @PathVariable String key,
                                                     @RequestBody String content) {
        String base = deps.url(deps.localstack()) + "/" + bucket;
        try {
            http.put().uri(base).retrieve().toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() != 409) {
                throw e;
            }
        }
        http.put().uri(base + "/" + key).contentType(MediaType.TEXT_PLAIN).body(content).retrieve().toBodilessEntity();
        return ResponseEntity.status(201).body(Map.of("bucket", bucket, "key", key));
    }

    @GetMapping("/s3/{bucket}/{key}")
    public ResponseEntity<Map<String, String>> s3Get(@PathVariable String bucket, @PathVariable String key) {
        try {
            String content = http.get().uri(deps.url(deps.localstack()) + "/" + bucket + "/" + key).retrieve().body(String.class);
            return ResponseEntity.ok(Map.of("bucket", bucket, "key", key, "content", String.valueOf(content)));
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    /** 200 when the bucket exists — used to check the localstack dataDir init script ran. */
    @GetMapping("/s3/{bucket}")
    public ResponseEntity<Void> s3Bucket(@PathVariable String bucket) {
        try {
            http.head().uri(deps.url(deps.localstack()) + "/" + bucket).retrieve().toBodilessEntity();
            return ResponseEntity.ok().build();
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    // ---- Solr

    @PostMapping("/solr/{collection}")
    public ResponseEntity<Map<String, String>> solrIndex(@PathVariable String collection, @RequestBody Map<String, String> doc) {
        http.post().uri(deps.url(deps.solr()) + "/solr/{c}/update?commit=true", collection)
                .contentType(MediaType.APPLICATION_JSON).body(List.of(doc)).retrieve().toBodilessEntity();
        return ResponseEntity.status(201).body(doc);
    }

    @GetMapping("/solr/{collection}/{id}")
    public ResponseEntity<?> solrGet(@PathVariable String collection, @PathVariable String id) {
        JsonNode res = http.get().uri(deps.url(deps.solr()) + "/solr/{c}/select?q=id:{id}&wt=json", collection, id)
                .retrieve().body(JsonNode.class);
        JsonNode docs = res.path("response").path("docs");
        return docs.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(docs.get(0));
    }

    // ---- OpenSearch

    @PutMapping("/opensearch/{index}/{id}")
    public ResponseEntity<JsonNode> osIndex(@PathVariable String index, @PathVariable String id,
                                            @RequestBody Map<String, Object> doc) {
        JsonNode res = http.put().uri(deps.url(deps.opensearch()) + "/{i}/_doc/{id}?refresh=true", index, id)
                .contentType(MediaType.APPLICATION_JSON).body(doc).retrieve().body(JsonNode.class);
        return ResponseEntity.status(201).body(res);
    }

    @GetMapping("/opensearch/{index}/{id}")
    public ResponseEntity<?> osGet(@PathVariable String index, @PathVariable String id) {
        try {
            JsonNode res = http.get().uri(deps.url(deps.opensearch()) + "/{i}/_doc/{id}", index, id).retrieve().body(JsonNode.class);
            return ResponseEntity.ok(res.path("_source"));
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(e.getStatusCode().value())).build();
        }
    }
}
