package com.fraus.orderservice.kafka;

import com.fraus.commonlibs.kafka.DeliveryAssignedEvent;
import com.fraus.commonlibs.kafka.OrderPaidEvent;
import com.fraus.orderservice.domain.OrderProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@AllArgsConstructor
@EnableKafka
@Configuration
public class DeliveryAssignedKafkaConsumer {
    
    private final OrderProcessor orderProcessor;

    @KafkaListener(
            topics = "${delivery-assigned-topic}",
            containerFactory = "deliveryAssignedEventListenerFactory"
    )
    public void listen(DeliveryAssignedEvent event) {
        log.info("Received delivery assigned event: {}", event);
        orderProcessor.processDeliveryAssigned(event);
    }
}
