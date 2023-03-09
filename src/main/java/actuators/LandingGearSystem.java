package actuators;

import com.rabbitmq.client.Connection;
import enums.PlaneMode;
import utils.Actuators;
import utils.Formats;

import java.io.IOException;

public class LandingGearSystem extends Actuator {
    public LandingGearSystem(Connection connection) throws IOException {
        super(connection);
    }

    @Override
    protected String getActuatorName() {
        return Actuators.LANDING_GEAR;
    }

    @Override
    public void handle(byte[] message) {
        PlaneMode mode = PlaneMode.fromByte(message[0]);
        if (mode == PlaneMode.LANDING) {
            Formats.printActuator("Landing Gear System", " About to land, plane gear " +
                    "is opened...");
        }
    }
}
