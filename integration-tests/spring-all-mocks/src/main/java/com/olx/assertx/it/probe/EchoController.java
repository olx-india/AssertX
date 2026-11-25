package com.olx.assertx.it.probe;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

/** Reflects the request back, to verify AssertX's built-in request steps (methods, headers, params, bodies). */
@RestController
public class EchoController {

    @RequestMapping(value = "/echo", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.PATCH, RequestMethod.DELETE})
    public ResponseEntity<Map<String, Object>> echo(HttpServletRequest request,
                                                    @RequestParam Map<String, String> query,
                                                    @RequestHeader(value = "X-Trace-Id", required = false) String traceId,
                                                    @RequestBody(required = false) String body) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("method", request.getMethod());
        out.put("query", query);
        out.put("traceId", traceId);
        out.put("contentType", request.getContentType());
        out.put("body", body);
        return ResponseEntity.ok().header("X-Echo-Method", request.getMethod()).body(out);
    }

    @RequestMapping(value = "/echo", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        return ResponseEntity.noContent().header(HttpHeaders.ALLOW, "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS").build();
    }

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestPart("document") MultipartFile file) throws IOException {
        return Map.of(
                "filename", String.valueOf(file.getOriginalFilename()),
                "size", file.getSize(),
                "content", new String(file.getBytes(), StandardCharsets.UTF_8).trim());
    }

    @GetMapping("/moved")
    public ResponseEntity<Void> moved() {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(URI.create("/echo?redirected=true")).build();
    }
}
