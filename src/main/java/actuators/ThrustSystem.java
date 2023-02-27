package actuators;

import com.rabbitmq.client.Connection;
import utils.Actuators;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;

import static utils.Formats.ACTUATOR_LENGTH;
import static utils.Formats.ACTUATOR_NAME_STYLE;

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

        System.out.println(Functions.formatColorReset(ACTUATOR_NAME_STYLE + Functions.center(
                "Thrust System", ACTUATOR_LENGTH), 1) + " Lowering the altitude by " + change + "m");
        publishChange(Sensors.ALTITUDE, Functions.shortToBytes(change));
    }
}
