package EventManager;

import channels.ChannelFactory;
import channels.InBoundChannel;
import channels.OutBoundChannel;
import com.rabbitmq.client.Connection;
import utils.Colors;
import utils.Exchanges;
import utils.Functions;
import utils.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EventManager implements Runnable {
    private final InBoundChannel channelIn;

    private final OutBoundChannel channelOut;

    private final ScheduledExecutorService scheduler;
    public final HashMap<Integer, Log> logStore;
    private final Lock logLock;

    public EventManager(Connection connection, ScheduledExecutorService scheduler) throws IOException {
        channelIn = ChannelFactory.newInBoundChannel(connection, Exchanges.ACTUATOR_RECEIVED,
                List.of(""));
        channelOut = ChannelFactory.newOutBoundChannel(connection, Exchanges.ACTUATOR_INPUT);

        this.scheduler = scheduler;
        logStore = new HashMap<>();
        logLock = new ReentrantLock(true);
    }

    @Override
    public void run() {
        try {
            channelIn.consume((consumerTag, delivery) -> {
                byte[] body = delivery.getBody();
                int id = Functions.bytesToInt(body);

                logReceived(id);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void publishAction(int id, String actuator, byte[] change) {
        try {
            byte[] idInBytes = Functions.intToBytes(id);
            byte[] result = Functions.concatenateByteArrays(idInBytes, change);
            // the message sent will be in format id:result, where id in the first four bytes
            channelOut.publish(result, actuator);
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
                            "received, resending..."));
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
