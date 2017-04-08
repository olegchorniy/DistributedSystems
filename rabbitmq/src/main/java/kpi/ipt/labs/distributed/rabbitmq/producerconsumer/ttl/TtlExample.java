package kpi.ipt.labs.distributed.rabbitmq.producerconsumer.ttl;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TtlExample {

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            prepareAmqpEntities(channel);

            publish(channel, "[Without-TTL] Message #1", false);
            publish(channel, "[TTL] Message #2", true);
            publish(channel, "[Without-TTL] Message #3", false);

            System.out.println(get(channel, false));
            System.out.println(get(channel, true));
            System.out.println(get(channel, false));

            channel.close();
        }
    }

    private static void prepareAmqpEntities(Channel channel) throws IOException {
        channel.queueDeclare(TtlConstants.QUEUE_NAME, false, false, false, null);
        channel.exchangeDeclare(TtlConstants.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        channel.queueBind(TtlConstants.QUEUE_NAME, TtlConstants.EXCHANGE_NAME, TtlConstants.ROUTING_KEY);
    }

    private static void publish(Channel channel, String message, boolean withTtl) throws IOException {
        AMQP.BasicProperties ttlProperties = null;

        if (withTtl) {
            ttlProperties = new AMQP.BasicProperties.Builder()
                    .expiration(String.valueOf(TtlConstants.TTL))
                    .build();
        }

        channel.basicPublish(TtlConstants.EXCHANGE_NAME, TtlConstants.ROUTING_KEY, ttlProperties, message.getBytes());

        System.out.println("Sent '" + message + "'");
    }

    private static String get(Channel channel, boolean withSleep) throws IOException {
        if (withSleep) {
            try {
                Thread.sleep(TtlConstants.TTL + 100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        GetResponse response = channel.basicGet(TtlConstants.QUEUE_NAME, true);
        if (response == null) {
            return null;
        }

        return new String(response.getBody(), StandardCharsets.UTF_8);
    }
}
