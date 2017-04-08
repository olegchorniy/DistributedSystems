package kpi.ipt.labs.distributed.rabbitmq.publishsubscribe;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import kpi.ipt.labs.distributed.rabbitmq.common.FileCounter;

import java.nio.file.Paths;

import static kpi.ipt.labs.distributed.rabbitmq.publishsubscribe.PublishSubscribeConstants.EXCHANGE_NAME;

public class Publisher {

    public static void main(String[] args) throws Exception {

        FileCounter counter = new FileCounter(Paths.get("D:", "work_dir", "amqp_counter.txt"));

        ConnectionFactory factory = new ConnectionFactory();

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

            String message = "[Publisher/Subscriber] Hello World #" + counter.getAndIncrement();
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());

            System.out.println("Sent '" + message + "'");
            channel.close();
        }
    }
}
