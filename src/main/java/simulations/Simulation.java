package simulations;

import channels.ChannelFactory;
import channels.OutBoundChannel;
import com.rabbitmq.client.Connection;
import utils.Exchanges;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Simulation implements Runnable {
    protected final OutBoundChannel channel;

    protected final ScheduledExecutorService service;
    private boolean firstTime = true;

    public Simulation(Connection connection, ScheduledExecutorService service) throws IOException {
        channel = ChannelFactory.newOutBoundChannel(connection, Exchanges.SENSOR_INPUT);
        this.service = service;
    }

    @Override
    public void run() {
        simulateWrapper();
    }

    protected void publishChange(String outputSensor, byte[] change) {
        try {
            channel.publish(change, outputSensor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void simulateWrapper() {
        if (toContinue()) {
            if (!firstTime) {
                simulate();
            }
            firstTime = false;
            service.schedule(this::simulateWrapper, getIntervalInMillis(), TimeUnit.MILLISECONDS);
        }
    }

    protected abstract void simulate();

    protected abstract boolean toContinue();

    protected abstract int getIntervalInMillis();
}
