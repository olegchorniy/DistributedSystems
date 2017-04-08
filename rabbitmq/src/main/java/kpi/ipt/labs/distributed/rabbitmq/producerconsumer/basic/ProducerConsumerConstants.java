package kpi.ipt.labs.distributed.rabbitmq.producerconsumer.basic;

public abstract class ProducerConsumerConstants {

    private ProducerConsumerConstants() {
    }

    public static final String EXCHANGE_NAME = "my.producer-consumer.exchange";
    public static final String QUEUE_NAME = "my.producer-consumer.queue";
    public static final String ROUTING_KEY = "my.producer-consumer.key";
}
