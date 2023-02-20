package actuators;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import utils.Exchanges;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class Actuator implements Runnable {
    private final Channel channelIn;
    private final String queueNameIn;

    private final Channel channelOut;

    public Actuator(Connection connection) throws IOException, TimeoutException {
        channelIn = connection.createChannel();
        channelIn.exchangeDeclare(Exchanges.ACTUATOR_INPUT, "direct");
        queueNameIn = channelIn.queueDeclare().getQueue();

        channelIn.queueBind(queueNameIn, Exchanges.ACTUATOR_INPUT, getActuatorName());

        channelOut = connection.createChannel();
        channelOut.exchangeDeclare(Exchanges.SENSOR_INPUT, "direct");
    }

    public void run() {
        try {
            channelIn.basicConsume(queueNameIn, true, onReceive(), consumerTag -> {
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

    public abstract DeliverCallback onReceive();
}
