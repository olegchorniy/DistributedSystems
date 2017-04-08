package kpi.ipt.labs.distributed.rabbitmq.producerconsumer.basic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import kpi.ipt.labs.distributed.rabbitmq.common.FileCounter;
import kpi.ipt.labs.distributed.rabbitmq.common.MonotonicCounter;

import java.nio.file.Paths;

public class Producer {

    public static void main(String[] args) throws Exception {

        MonotonicCounter counter = new FileCounter(Paths.get("D:", "work_dir", "amqp_counter.txt"));

        ConnectionFactory factory = new ConnectionFactory();

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(ProducerConsumerConstants.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            String message = "[Producer/Consumer] Message #" + counter.getAndIncrement();
            channel.basicPublish(ProducerConsumerConstants.EXCHANGE_NAME, ProducerConsumerConstants.ROUTING_KEY, null, message.getBytes());

            System.out.println("Sent '" + message + "'");
            channel.close();
        }
    }
}
