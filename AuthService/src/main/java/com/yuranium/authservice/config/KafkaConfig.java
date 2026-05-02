package com.yuranium.authservice.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig
{
    private final Environment environment;

    @Bean
    public NewTopic userDeleteTopic()
    {
        return new NewTopic(
                environment.getProperty("kafka.topic-names.user-delete"),
                2, (short) 2);
    }

    @Bean
    public NewTopic userCreatedTopic()
    {
        return new NewTopic(
                environment.getProperty("kafka.topic-names.user-create"),
                1, (short) 2
        );
    }

    @Bean
    public NewTopic userUpdatedTopic()
    {
        return new NewTopic(
                environment.getProperty("kafka.topic-names.user-update"),
                1, (short) 2
        );
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> factory)
    {
        return new KafkaTemplate<>(factory);
    }

    @Bean
    ProducerFactory<String, Object> producerFactory()
    {
        Map<String, Object> settings = new HashMap<>();

        settings.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.producer.bootstrap-servers"));
        settings.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        settings.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        settings.put(ProducerConfig.RETRIES_CONFIG,
                environment.getProperty("spring.kafka.producer.retries"));
        settings.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                environment.getProperty("spring.kafka.producer.properties.enable.idempotence"));
        settings.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,
                environment.getProperty("spring.kafka.producer.transaction-id-prefix"));
        settings.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");

        return new DefaultKafkaProducerFactory<>(settings);
    }

    @Bean
    KafkaTransactionManager<String, Object> kafkaTransactionManager(
            ProducerFactory<String, Object> producerFactory)
    {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @Bean
    @Primary
    JpaTransactionManager transactionManager(EntityManagerFactory managerFactory)
    {
        return new JpaTransactionManager(managerFactory);
    }
}