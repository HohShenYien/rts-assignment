package actuators;

import benchmark.TimeManager;
import channels.ChannelFactory;
import channels.InBoundChannel;
import channels.OutBoundChannel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Delivery;
import utils.Exchanges;
import utils.Functions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public abstract class Actuator implements Runnable {
    private final InBoundChannel channelIn;

    private final OutBoundChannel channelOut;
    private final OutBoundChannel channelReceived;

    public Actuator(Connection connection) throws IOException {
        channelIn = ChannelFactory.newInBoundChannel(connection, Exchanges.ACTUATOR_INPUT,
                List.of(getActuatorName()));

        channelOut = ChannelFactory.newOutBoundChannel(connection, Exchanges.SENSOR_INPUT);
        channelReceived = ChannelFactory.newOutBoundChannel(connection,
                Exchanges.ACTUATOR_RECEIVED);
    }

    public void run() {
        try {
            channelIn.consume(this::onReceive);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void publishChange(String outputSensor, byte[] change) {
        try {
            channelOut.publish(change, outputSensor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract String getActuatorName();

    private void onReceive(String consumerTag, Delivery delivery) {
        long nowInMillis = System.currentTimeMillis();

        byte[] body = delivery.getBody();
        // first 8 bytes are time in millis
        byte[] startTimeInMillis = Arrays.copyOfRange(body, 0, 8);
        long duration = nowInMillis - Functions.bytesToLong(startTimeInMillis);
        if (duration > 5) {
            System.out.println("WOI " + delivery.getEnvelope().getRoutingKey());
        }
        TimeManager.addDuration(duration);
        // next 4 bytes are id
        byte[] id = Arrays.copyOfRange(body, 8, 12);
        byte[] message = Arrays.copyOfRange(body, 12, body.length);
        try {
            channelReceived.publish(id, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        handle(message);
    }

    public abstract void handle(byte[] message);
}
