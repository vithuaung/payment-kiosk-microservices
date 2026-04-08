package com.conversion.pmk.notification.config;

import com.conversion.pmk.notification.event.PatientCheckinEvent;
import com.conversion.pmk.notification.event.PaymentCompletedEvent;
import com.conversion.pmk.notification.event.PaymentFailedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    // Shared base properties for all consumer factories.
    // Do NOT include JsonDeserializer properties here — they are set on each deserializer instance.
    private Map<String, Object> baseProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    // Factory for PaymentCompletedEvent
    @Bean
    public ConsumerFactory<String, PaymentCompletedEvent> completedEventConsumerFactory() {
        Map<String, Object> props = baseProps();
        JsonDeserializer<PaymentCompletedEvent> deserializer =
                new JsonDeserializer<>(PaymentCompletedEvent.class, false);
        deserializer.addTrustedPackages("com.conversion.pmk.*");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> completedEventListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(completedEventConsumerFactory());
        return factory;
    }

    // Factory for PaymentFailedEvent
    @Bean
    public ConsumerFactory<String, PaymentFailedEvent> failedEventConsumerFactory() {
        Map<String, Object> props = baseProps();
        JsonDeserializer<PaymentFailedEvent> deserializer =
                new JsonDeserializer<>(PaymentFailedEvent.class, false);
        deserializer.addTrustedPackages("com.conversion.pmk.*");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent> failedEventListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(failedEventConsumerFactory());
        return factory;
    }

    // Default factory for PatientCheckinEvent (used by @KafkaListener without containerFactory)
    @Bean
    public ConsumerFactory<String, PatientCheckinEvent> patientCheckinConsumerFactory() {
        Map<String, Object> props = baseProps();
        JsonDeserializer<PatientCheckinEvent> deserializer =
                new JsonDeserializer<>(PatientCheckinEvent.class, false);
        deserializer.addTrustedPackages("com.conversion.pmk.*");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PatientCheckinEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PatientCheckinEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(patientCheckinConsumerFactory());
        return factory;
    }
}
