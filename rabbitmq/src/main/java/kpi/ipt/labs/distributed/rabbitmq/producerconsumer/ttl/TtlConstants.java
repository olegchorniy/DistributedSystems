package kpi.ipt.labs.distributed.rabbitmq.producerconsumer.ttl;

public abstract class TtlConstants {

    private TtlConstants() {
    }

    public static final String EXCHANGE_NAME = "my.producer-consumer.ttl.exchange";
    public static final String QUEUE_NAME = "my.producer-consumer.ttl.queue";
    public static final String ROUTING_KEY = "my.producer-consumer.ttl.key";

    public static final int TTL = 2000;
}
