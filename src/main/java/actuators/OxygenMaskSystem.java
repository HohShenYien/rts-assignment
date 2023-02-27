package actuators;

import com.rabbitmq.client.Connection;
import enums.OxygenMaskMode;
import utils.Actuators;
import utils.Functions;

import java.io.IOException;

import static utils.Formats.ACTUATOR_LENGTH;
import static utils.Formats.ACTUATOR_NAME_STYLE;

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
        System.out.println(Functions.formatColorReset(ACTUATOR_NAME_STYLE + Functions.center(
                "Oxygen Mask System", ACTUATOR_LENGTH), 1) + " " + action);
    }
}
