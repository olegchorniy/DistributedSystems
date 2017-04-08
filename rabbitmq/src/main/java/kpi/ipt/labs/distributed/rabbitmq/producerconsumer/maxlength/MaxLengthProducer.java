package kpi.ipt.labs.distributed.rabbitmq.producerconsumer.maxlength;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import kpi.ipt.labs.distributed.rabbitmq.common.InMemoryCounter;
import kpi.ipt.labs.distributed.rabbitmq.common.MonotonicCounter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class MaxLengthProducer {

    public static void main(String[] args) throws Exception {

        MonotonicCounter counter = new InMemoryCounter();

        ConnectionFactory factory = new ConnectionFactory();

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            prepareAmqpEntities(channel);

            for (int i = 0; i < MaxLengthConstants.MAX_LENGTH + 1; i++) {
                String message = "[Max-Length] Message #" + counter.getAndIncrement();
                channel.basicPublish(MaxLengthConstants.EXCHANGE_NAME, MaxLengthConstants.ROUTING_KEY, null, message.getBytes());

                System.out.println("Sent '" + message + "'");
            }

            channel.close();
        }
    }

    private static void prepareAmqpEntities(Channel channel) throws IOException {
        Map<String, Object> queueArgs = Collections.singletonMap("x-max-length", MaxLengthConstants.MAX_LENGTH);
        channel.queueDeclare(MaxLengthConstants.QUEUE_NAME, false, false, false, queueArgs);
        channel.exchangeDeclare(MaxLengthConstants.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        channel.queueBind(MaxLengthConstants.QUEUE_NAME, MaxLengthConstants.EXCHANGE_NAME, MaxLengthConstants.ROUTING_KEY);
    }
}
