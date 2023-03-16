package simulations;

import com.rabbitmq.client.Connection;
import enums.PlaneMode;
import utils.Colors;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

public class LandingSimulation extends Simulation {
    private boolean simulated = false;

    public LandingSimulation(Connection connection, ScheduledExecutorService service) throws IOException,
            TimeoutException {
        super(connection, service);
    }

    @Override
    protected void simulate() {
        System.out.println(Functions.formatColorReset(Colors.RED + "\nAirplane is landing now!"));
        publishChange(Sensors.LANDING_MODE, PlaneMode.toBytes(PlaneMode.LANDING));
        simulated = true;

        // Stop simulating after landing
        service.shutdownNow();
    }

    @Override
    protected boolean toContinue() {
        return !simulated;
    }

    @Override
    // Plane lands after 20s
    protected int getIntervalInMillis() {
        return 20 * 60 * 1000;
    }
}
