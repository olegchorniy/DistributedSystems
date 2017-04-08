package kpi.ipt.labs.distributed.rabbitmq.producerconsumer.maxlength;

import com.rabbitmq.client.*;

import java.io.IOException;

public class MaxLengthConsumer {

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.basicConsume(MaxLengthConstants.QUEUE_NAME, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("Received '" + new String(body, "UTF-8") + "'");
            }
        });
    }
}
