package channels;

import com.rabbitmq.client.Connection;
import utils.Functions;

import java.io.IOException;

public class OutBoundChannel extends AbstractChannel {
    public OutBoundChannel(Connection connection, String exchangeName) throws IOException {
        super(connection, exchangeName);
    }

    public void publish(byte[] message, String routeKey) throws IOException {
        byte[] timeInBytes = Functions.longToBytes(System.nanoTime());
        // message in the form of time(8):length(4):routeKey(length):message
        byte[] result = Functions.concatenateByteArrays(timeInBytes, message);
        channel.basicPublish("Topic", exchangeName + "." + routeKey, null, result);
    }
}
