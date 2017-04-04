package kpi.ipt.labs.distributed.rabbitmq.direct;

import com.rabbitmq.client.*;

import java.io.IOException;

import static kpi.ipt.labs.distributed.rabbitmq.direct.DirectConstants.*;

public class DirectConsumer {

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, new DefaultConsumer(channel) {
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
