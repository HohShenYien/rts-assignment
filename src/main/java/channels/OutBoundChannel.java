package channels;

import com.rabbitmq.client.Connection;
import utils.Functions;

import java.io.IOException;

public class OutBoundChannel extends AbstractChannel {
    public OutBoundChannel(Connection connection, String exchangeName) throws IOException {
        super(connection, exchangeName);
    }

    public void publish(byte[] message, String routeKey) throws IOException {
        byte[] timeInBytes = Functions.longToBytes(System.currentTimeMillis());
        byte[] result = Functions.concatenateByteArrays(timeInBytes, message);
        channel.basicPublish(exchangeName, routeKey, null, result);
    }
}
