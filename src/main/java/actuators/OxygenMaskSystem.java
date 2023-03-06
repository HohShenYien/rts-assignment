package actuators;

import com.rabbitmq.client.Connection;
import enums.OxygenMaskMode;
import utils.Actuators;
import utils.Formats;

import java.io.IOException;

public class OxygenMaskSystem extends Actuator {
    public OxygenMaskSystem(Connection connection) throws IOException {
        super(connection);
    }

    @Override
    protected String getActuatorName() {
        return Actuators.OXYGEN_MASK;
    }

    @Override
    public void handle(byte[] message) {
        OxygenMaskMode action = OxygenMaskMode.fromByte(message[0]);
        Formats.printActuator("Oxygen Mask System", " " + action);
    }
}
