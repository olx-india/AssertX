package com.olx.assertx.it.probe;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final Deps deps;

    public KafkaController(Deps deps) {
        this.deps = deps;
    }

    private Properties base() {
        Properties p = new Properties();
        p.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, deps.host() + ":" + deps.kafka());
        p.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        p.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 10000);
        return p;
    }

    /** Topics created from api-testing.yml (kafka.topics). */
    @GetMapping("/topics")
    public Set<String> topics() throws Exception {
        try (Admin admin = Admin.create(base())) {
            return new TreeSet<>(admin.listTopics().names().get(10, TimeUnit.SECONDS));
        }
    }

    @PostMapping("/topics/{topic}")
    public ResponseEntity<Map<String, Object>> publish(@PathVariable String topic, @RequestBody String message)
            throws Exception {
        Properties p = base();
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 10000);
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(p)) {
            long offset = producer.send(new ProducerRecord<>(topic, message)).get(10, TimeUnit.SECONDS).offset();
            return ResponseEntity.status(201).body(Map.of("topic", topic, "offset", offset));
        }
    }

    /** Last message on partition 0, read from the beginning of the topic. */
    @GetMapping("/topics/{topic}/last")
    public ResponseEntity<String> last(@PathVariable String topic) {
        Properties p = base();
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(p)) {
            TopicPartition partition = new TopicPartition(topic, 0);
            consumer.assign(List.of(partition));
            consumer.seekToBeginning(List.of(partition));
            long end = consumer.endOffsets(List.of(partition)).get(partition);
            String last = null;
            long deadline = System.currentTimeMillis() + 10_000;
            while (consumer.position(partition) < end && System.currentTimeMillis() < deadline) {
                for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(500))) {
                    last = record.value();
                }
            }
            return last == null ? ResponseEntity.notFound().build()
                    : ResponseEntity.ok().header("Content-Type", "application/json").body(last);
        }
    }
}
