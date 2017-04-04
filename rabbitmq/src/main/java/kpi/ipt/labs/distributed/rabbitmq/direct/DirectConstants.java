package kpi.ipt.labs.distributed.rabbitmq.direct;

public abstract class DirectConstants {
    private DirectConstants() {
    }

    public static final String EXCHANGE_NAME = "my.direct.exchange";
    public static final String QUEUE_NAME = "my.direct.queue";
    public static final String ROUTING_KEY = "my.direct.key";
}
