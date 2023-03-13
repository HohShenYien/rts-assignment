package sensors;

import channels.ChannelFactory;
import channels.InBoundChannel;
import channels.OutBoundChannel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import utils.Exchanges;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class Sensor implements Runnable {
    protected InBoundChannel channelIn;
    protected OutBoundChannel channelOut;

    public Sensor(Connection connection) throws IOException, TimeoutException {
        channelIn = ChannelFactory.newInBoundChannel(connection, Exchanges.SENSOR_INPUT,
                List.of(getSensorName()));

        channelOut = ChannelFactory.newOutBoundChannel(connection, Exchanges.SENSOR_OUTPUT);
    }

    public void run() {
        try {
            channelIn.consume(onReceive());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void publish(byte[] change) throws IOException {
        channelOut.publish(change, getSensorName());
    }

    public abstract DeliverCallback onReceive() throws IOException;

    protected abstract String getSensorName();
}
