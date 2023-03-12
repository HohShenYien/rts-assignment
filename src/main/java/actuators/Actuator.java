package actuators;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Delivery;
import utils.Exchanges;

import java.io.IOException;
import java.util.Arrays;

public abstract class Actuator implements Runnable {
    private final Channel channelIn;
    private final String queueNameIn;

    private final Channel channelOut;
    private final Channel channelReceived;

    public Actuator(Connection connection) throws IOException {
        channelIn = connection.createChannel();
        channelIn.exchangeDeclare(Exchanges.ACTUATOR_INPUT, "direct");
        queueNameIn = channelIn.queueDeclare().getQueue();

        channelIn.queueBind(queueNameIn, Exchanges.ACTUATOR_INPUT, getActuatorName());

        channelOut = connection.createChannel();
        channelOut.exchangeDeclare(Exchanges.SENSOR_INPUT, "direct");

        channelReceived = connection.createChannel();
        channelReceived.exchangeDeclare(Exchanges.ACTUATOR_RECEIVED, "direct");
    }

    public void run() {
        try {
            channelIn.basicConsume(queueNameIn, true, this::onReceive, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void publishChange(String outputSensor, byte[] change) {
        try {
            channelOut.basicPublish(Exchanges.SENSOR_INPUT, outputSensor, null, change);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract String getActuatorName();

    private void onReceive(String consumerTag, Delivery delivery) {
        byte[] body = delivery.getBody();
        // first 4 bytes are id
        byte[] id = Arrays.copyOfRange(body, 0, 4);
        byte[] message = Arrays.copyOfRange(body, 4, body.length);
        try {
            channelReceived.basicPublish(Exchanges.ACTUATOR_RECEIVED, "", null, id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        handle(message);
    }

    public abstract void handle(byte[] message);
}
