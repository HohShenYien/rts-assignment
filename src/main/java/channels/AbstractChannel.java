package channels;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

public abstract class AbstractChannel {
    protected final Channel channel;
    protected final String queueName;
    protected final String exchangeName;

    public AbstractChannel(Connection connection, String exchangeName) throws IOException {
        channel = connection.createChannel();
        channel.exchangeDeclare("Topic", "topic");
        queueName = channel.queueDeclare().getQueue();
        this.exchangeName = exchangeName;
    }
}
