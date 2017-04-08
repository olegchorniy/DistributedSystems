package kpi.ipt.labs.distributed.rabbitmq.producerconsumer.durable;

import com.rabbitmq.client.*;
import kpi.ipt.labs.distributed.rabbitmq.common.FileCounter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DurableProducer {

    private static final Path counterPath = Paths.get("D:", "work_dir", "amqp_counter.txt");

    public static void main(String[] args) throws Exception {

        FileCounter counter = new FileCounter(counterPath);
        ConnectionFactory factory = new ConnectionFactory();

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            prepareAmqpEntities(channel);

            String message = "[Durable] Message #" + counter.getAndIncrement();
            AMQP.BasicProperties properties = MessageProperties.PERSISTENT_TEXT_PLAIN;

            channel.basicPublish(
                    DurableConstants.EXCHANGE_NAME,
                    DurableConstants.ROUTING_KEY,
                    properties,
                    message.getBytes()
            );

            System.out.println("Sent '" + message + "'");

            channel.close();
        }
    }

    private static void prepareAmqpEntities(Channel channel) throws IOException {
        boolean durable = true;
        channel.queueDeclare(DurableConstants.QUEUE_NAME, durable, false, false, null);
        channel.exchangeDeclare(DurableConstants.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        channel.queueBind(DurableConstants.QUEUE_NAME, DurableConstants.EXCHANGE_NAME, DurableConstants.ROUTING_KEY);
    }
}
