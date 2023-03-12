import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import utils.Colors;
import utils.Exchanges;
import utils.Functions;
import utils.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EventManager implements Runnable {
    private final Channel channelIn;
    private final String queueNameIn;

    private final Channel channelOut;

    private final ScheduledExecutorService scheduler;
    private final HashMap<Integer, Log> logStore;
    private final Lock logLock;

    public EventManager(Connection connection, ScheduledExecutorService scheduler) throws IOException {
        channelIn = connection.createChannel();
        channelIn.exchangeDeclare(Exchanges.SENSOR_RECEIVED, "direct");
        queueNameIn = channelIn.queueDeclare().getQueue();
        channelIn.queueBind(queueNameIn, Exchanges.SENSOR_RECEIVED, "");

        channelOut = connection.createChannel();
        channelOut.exchangeDeclare(Exchanges.ACTUATOR_INPUT, "direct");

        this.scheduler = scheduler;
        logStore = new HashMap<>();
        logLock = new ReentrantLock(true);
    }

    @Override
    public void run() {
        try {
            channelIn.basicConsume(queueNameIn, true, (consumerTag, delivery) -> {
                byte[] body = delivery.getBody();
                int id = Functions.bytesToInt(body);

                logReceived(id);
            }, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void publishAction(int id, String actuator, byte[] change) {
        try {
            byte[] idInBytes = Functions.intToBytes(id);
            byte[] timeInBytes = Functions.longToBytes(System.currentTimeMillis());
            byte[] result = Functions.concatenateByteArrays(timeInBytes,
                    Functions.concatenateByteArrays(idInBytes, change));
            // the message sent will be in format millis(8):id(4):result
            channelOut.basicPublish(Exchanges.ACTUATOR_INPUT, actuator, null, result);
            checkEventReceived(id, actuator, change);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkEventReceived(int id, String actuator, byte[] change) {
        // Scheduler will be shut down after simulation ends, so checking here
        if (!scheduler.isShutdown()) {
            scheduler.schedule(() -> {
                if (!isLogReceived(id)) {
                    System.out.println(Functions.formatColorReset(Colors.RED + "Message not " +
                            "received, resending..." + "-" + id));
                    publishAction(id, actuator, change);
                }
            }, 200, TimeUnit.MILLISECONDS);
        }
    }

    private void logReceived(int id) {
        logLock.lock();
        Log log = logStore.get(id);
        Log newLog = log.logReceived();
        logStore.put(id, newLog);
        logLock.unlock();
    }

    private boolean isLogReceived(int id) {
        boolean result = false;
        logLock.lock();
        if (logStore.containsKey(id)) {
            Log log = logStore.get(id);
            result = log.received();
        }
        logLock.unlock();
        return result;
    }

    public void addLog(int id, byte[] message, String key) {
        Log log = new Log(message, key, false);
        logLock.lock();
        logStore.put(id, log);
        logLock.unlock();
    }
}
