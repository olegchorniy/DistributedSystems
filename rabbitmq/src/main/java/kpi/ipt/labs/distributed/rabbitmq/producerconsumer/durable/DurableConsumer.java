package kpi.ipt.labs.distributed.rabbitmq.producerconsumer.durable;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

public class DurableConsumer {

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();

            GetResponse response = channel.basicGet(DurableConstants.QUEUE_NAME, true);

            if (response == null) {
                System.out.println("There are no messages in the queue");
            } else {
                byte[] body = response.getBody();
                System.out.println("Received '" + new String(body, "UTF-8") + "'");
            }
        }
    }
}
