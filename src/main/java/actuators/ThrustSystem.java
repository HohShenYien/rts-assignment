package actuators;

import com.rabbitmq.client.Connection;
import utils.Actuators;
import utils.Formats;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;

public class ThrustSystem extends Actuator {
    public ThrustSystem(Connection connection) throws IOException {
        super(connection);
    }

    @Override
    protected String getActuatorName() {
        return Actuators.THRUST_SYSTEM;
    }

    @Override
    public void handle(byte[] message) {
        short change = Functions.bytesToShort(message);

        Formats.printActuator("Thrust System", " Lowering the altitude by " + change + "m");
        publishChange(Sensors.ALTITUDE, Functions.shortToBytes(change));
    }
}
