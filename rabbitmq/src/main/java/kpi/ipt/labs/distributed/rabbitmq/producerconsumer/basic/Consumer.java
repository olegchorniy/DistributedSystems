package kpi.ipt.labs.distributed.rabbitmq.producerconsumer.basic;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer {

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(ProducerConsumerConstants.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(ProducerConsumerConstants.QUEUE_NAME, false, false, false, null);
        channel.queueBind(ProducerConsumerConstants.QUEUE_NAME, ProducerConsumerConstants.EXCHANGE_NAME, ProducerConsumerConstants.ROUTING_KEY);

        boolean autoAck = false;
        channel.basicConsume(ProducerConsumerConstants.QUEUE_NAME, autoAck, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("Received '" + new String(body, "UTF-8") + "'");

                /* Use this only with autoAck = false */
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
    }
}
