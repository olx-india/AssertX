package com.olx.assertx.it.probe;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

final class Http {

    private Http() {
    }

    /**
     * HTTP/1.1 only: the JDK client otherwise attempts an h2c upgrade, which Solr's Jetty answers by
     * cancelling the stream (RST_STREAM) on POSTs.
     */
    static RestClient client(Duration readTimeout) {
        HttpClient jdk = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(jdk);
        factory.setReadTimeout(readTimeout);
        return RestClient.builder().requestFactory(factory).build();
    }
}
