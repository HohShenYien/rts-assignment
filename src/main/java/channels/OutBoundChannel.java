package channels;

import com.rabbitmq.client.Connection;
import utils.Functions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class OutBoundChannel extends AbstractChannel {
    public OutBoundChannel(Connection connection, String exchangeName) throws IOException {
        super(connection, exchangeName);
    }

    public void publish(byte[] message, String routeKey) throws IOException {
        byte[] timeInBytes = Functions.longToBytes(System.nanoTime());
        byte[] routeKeyInBytes = routeKey.getBytes(StandardCharsets.UTF_8);
        byte[] routeKeySize = Functions.shortToBytes((short) routeKeyInBytes.length);
        // message in the form of time(8):length(4):routeKey(length):message
        byte[] result =
                Functions.concatenateByteArrays(
                        Functions.concatenateByteArrays(
                                Functions.concatenateByteArrays(timeInBytes, routeKeySize),
                                routeKeyInBytes),
                        message);
        channel.basicPublish(exchangeName, "", null, result);
    }
}
