package actuators;

import com.rabbitmq.client.Connection;
import utils.Actuators;
import utils.Formats;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThrustSystem extends Actuator {
    private final ScheduledExecutorService scheduler;

    public ThrustSystem(Connection connection, ScheduledExecutorService scheduler) throws IOException {
        super(connection);
        this.scheduler = scheduler;
    }

    @Override
    protected String getActuatorName() {
        return Actuators.THRUST_SYSTEM;
    }

    @Override
    public void handle(byte[] message) {
        short change = Functions.bytesToShort(message);

        Formats.printActuator("Thrust System", " " + (change > 0 ?
                "Raising" : "Lowering") + " the altitude by " + Math.abs(change) +
                "m");

        scheduler.schedule(() -> publishChange(Sensors.ALTITUDE, Functions.shortToBytes(change)), 200, TimeUnit.MILLISECONDS);
    }
}
