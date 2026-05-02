package com.yuranium.projectservice.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
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
    public NewTopic projectDeleteTopic(
            @Value("${kafka.topic-names.project-delete}")
            String topicName)
    {
        return new NewTopic(topicName, 2, (short) 2);
    }

    @Bean
    public NewTopic projectsDeleteTopic(
            @Value("${kafka.topic-names.projects-delete}")
            String topicName)
    {
        return new NewTopic(topicName, 2, (short) 2);
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> factory)
    {
        return new KafkaTemplate<>(factory);
    }

    @Bean
    ConsumerFactory<String, Object> consumerFactory()
    {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.consumer.bootstrap-servers"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG,
                environment.getProperty("spring.kafka.consumer.isolation-level",
                        "READ_COMMITED").toLowerCase());

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory, KafkaTemplate<String, Object> kafkaTemplate
    )
    {
        DefaultErrorHandler handler = new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate));

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(handler);
        return factory;
    }

    @Bean
    ProducerFactory<String, Object> producerFactory()
    {
        Map<String, Object> settings = new HashMap<>();

        settings.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.consumer.bootstrap-servers"));
        settings.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        settings.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        settings.put(ProducerConfig.RETRIES_CONFIG,
                environment.getProperty("spring.kafka.producer.retries"));
        settings.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                environment.getProperty("spring.kafka.producer.properties.enable.idempotence"));
        settings.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,
                environment.getProperty("spring.kafka.producer.transaction-id-prefix"));

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