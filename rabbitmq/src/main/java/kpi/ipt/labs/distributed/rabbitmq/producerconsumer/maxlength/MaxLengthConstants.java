package kpi.ipt.labs.distributed.rabbitmq.producerconsumer.maxlength;

public abstract class MaxLengthConstants {

    private MaxLengthConstants() {
    }

    public static final String EXCHANGE_NAME = "my.producer-consumer.max-length.exchange";
    public static final String QUEUE_NAME = "my.producer-consumer.max-length.queue";
    public static final String ROUTING_KEY = "my.producer-consumer.max-length.key";

    public static final int MAX_LENGTH = 10;
}
