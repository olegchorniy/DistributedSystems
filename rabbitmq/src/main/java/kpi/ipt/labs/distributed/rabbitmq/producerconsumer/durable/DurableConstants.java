package kpi.ipt.labs.distributed.rabbitmq.producerconsumer.durable;

public abstract class DurableConstants {

    private DurableConstants() {
    }

    public static final String EXCHANGE_NAME = "my.producer-consumer.durable.exchange";
    public static final String QUEUE_NAME = "my.producer-consumer.durable.queue";
    public static final String ROUTING_KEY = "my.producer-consumer.durable.key";
}
