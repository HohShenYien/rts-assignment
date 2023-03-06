package simulations;

import benchmark.TimeManager;
import com.rabbitmq.client.Connection;
import utils.Colors;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

public class TemperatureSimulation extends Simulation {
    private int timeRun;

    public TemperatureSimulation(Connection connection, ScheduledExecutorService service) throws IOException,
            TimeoutException {
        super(connection, service);
        timeRun = 0;
    }

    @Override
    protected void simulate() {
        int newTemp = 100;
        if (newTemp == 0) {
            return;
        }
        String event = newTemp < 0 ? "through clouds" : "under hot Sun";
        String change = newTemp > 0 ? "raised" : "decreased";

        System.out.println(Functions.formatColorReset(Colors.RED + "\nPassing " + event + "! " +
                "Temperature " + change + " by " + newTemp + "°C"));
        publishChange(Sensors.TEMPERATURE, Functions.shortToBytes((short) newTemp));
    }

    @Override
    protected boolean toContinue() {
        if (++timeRun < 1000) {
            return true;
        }
        TimeManager.printDuration();
        return false;
    }

    @Override
    protected int getIntervalInMillis() {
        return Functions.getRandom(1, 3) * 50;
    }
}
