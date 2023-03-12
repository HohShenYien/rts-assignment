package actuators;

import com.rabbitmq.client.Connection;
import utils.Actuators;
import utils.Formats;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

public class HeatingSystem extends Actuator {
    private final ScheduledExecutorService scheduler;

    public HeatingSystem(Connection connection, ScheduledExecutorService scheduler) throws IOException {
        super(connection);
        this.scheduler = scheduler;
    }

    @Override
    protected String getActuatorName() {
        return Actuators.HEATING_SYSTEM;
    }

    @Override
    public void handle(byte[] message) {

        short change = Functions.bytesToShort(message);

        Formats.printActuator("Heating System", " " + (change < 0 ? "Reducing" : "Raising")
                + " Temperature by " + Math.abs(change) + "°C");

        publishChange(Sensors.TEMPERATURE,
                Functions.shortToBytes(change));
    }
}
