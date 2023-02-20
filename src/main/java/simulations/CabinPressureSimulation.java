package simulations;

import com.rabbitmq.client.Connection;
import enums.CabinPressure;
import utils.Colors;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

public class CabinPressureSimulation extends Simulation {
    private boolean lowered = false;
    private boolean toSimulate = true;

    public CabinPressureSimulation(Connection connection, ScheduledExecutorService service) throws IOException, TimeoutException {
        super(connection, service);
    }

    @Override
    protected void simulate() {
        // simulating twice, pressure reduce once, back to normal once
        if (!lowered) {
            System.out.println(Functions.formatColorReset(Colors.RED + "\nA passenger broke the " +
                    "window! Cabin pressure is decreasing."));
            publishChange(Sensors.CABIN_PRESSURE, CabinPressure.toBytes(CabinPressure.LOW));
            lowered = true;
        } else {
            System.out.println(Functions.formatColorReset(Colors.RED + "\nA crew has fixed the " +
                    "window! Cabin pressure is back to normal"));
            publishChange(Sensors.CABIN_PRESSURE, CabinPressure.toBytes(CabinPressure.NORMAL));
            toSimulate = false;
        }
    }

    @Override
    protected boolean toContinue() {
        return toSimulate;
    }

    @Override
    protected int getIntervalInMillis() {
        return lowered ? Functions.getRandom(4, 8) * 1000 : 3000;
    }
}
