package channels;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;

public class InBoundChannel extends AbstractChannel {

    public InBoundChannel(Connection connection, String exchangeName) throws IOException {
        super(connection, exchangeName);
    }

    public void consume(DeliverCallback handler) throws IOException {
        channel.basicConsume(queueName, true, handler, consumerTag -> {
        });
    }

    public void bindQueue(String routeKey) throws IOException {
        channel.queueBind(queueName, exchangeName, routeKey);
    }
}
