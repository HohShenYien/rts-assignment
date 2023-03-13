package channels;

import com.rabbitmq.client.Connection;

import java.io.IOException;

public class ChannelFactory {
    public static InBoundChannel newInBoundChannel(Connection connection, String exchangeName,
                                                   Iterable<String> routeKeys) throws IOException {
        InBoundChannel inBoundChannel = new InBoundChannel(connection, exchangeName);
        for (String routeKey : routeKeys) {
            inBoundChannel.bindQueue(routeKey);
        }
        return inBoundChannel;
    }

    public static OutBoundChannel newOutBoundChannel(Connection connection, String exchangeName) throws IOException {
        return new OutBoundChannel(connection, exchangeName);
    }
}
