package channels;

import com.rabbitmq.client.Connection;

import java.io.IOException;

public class OutBoundChannel extends AbstractChannel {
    public OutBoundChannel(Connection connection, String exchangeName) throws IOException {
        super(connection, exchangeName);
    }

    public void publish(byte[] message, String routeKey) throws IOException {
        channel.basicPublish(exchangeName, routeKey, null, message);
    }
}
