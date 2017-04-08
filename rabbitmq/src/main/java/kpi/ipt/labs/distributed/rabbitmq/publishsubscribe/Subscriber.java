package kpi.ipt.labs.distributed.rabbitmq.publishsubscribe;

import com.rabbitmq.client.*;

import java.io.IOException;

import static kpi.ipt.labs.distributed.rabbitmq.publishsubscribe.PublishSubscribeConstants.EXCHANGE_NAME;


public class Subscriber {

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        for (String bindingKey : PublishSubscribeConstants.ROUTING_KEYS) {

            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);

            channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    System.out.printf(
                            "Received: queue = %s, binding key = %s, routing key = %s, message = '%s'%n",
                            queueName,
                            bindingKey,
                            envelope.getRoutingKey(),
                            new String(body, "UTF-8")
                    );
                }
            });
        }
    }
}
