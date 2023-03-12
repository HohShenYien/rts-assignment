package sensors;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import utils.Exchanges;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class Sensor implements Runnable {
    protected Channel channelIn;
    protected String queueNameIn;

    protected Channel channelOut;
    protected String queueNameOut;

    public Sensor(Connection connection) throws IOException, TimeoutException {
        channelIn = connection.createChannel();
        channelIn.exchangeDeclare(Exchanges.SENSOR_INPUT, "direct");
        queueNameIn = channelIn.queueDeclare().getQueue();
        channelIn.queueBind(queueNameIn, Exchanges.SENSOR_INPUT, getSensorName());

        channelOut = connection.createChannel();
        channelOut.exchangeDeclare(Exchanges.SENSOR_OUTPUT, "direct");
        queueNameOut = channelOut.queueDeclare().getQueue();
    }

    public void run() {
        try {
            channelIn.basicConsume(queueNameIn, true, onReceive(), consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void publish(byte[] change) throws IOException {
        channelOut.basicPublish(Exchanges.SENSOR_OUTPUT, getSensorName(), null, change);
    }

    public abstract DeliverCallback onReceive() throws IOException;

    protected abstract String getSensorName();
}
