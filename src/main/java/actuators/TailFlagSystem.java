package actuators;

import com.rabbitmq.client.Connection;
import enums.TailFlagAction;
import utils.Actuators;
import utils.Functions;

import java.io.IOException;

import static utils.Formats.ACTUATOR_LENGTH;
import static utils.Formats.ACTUATOR_NAME_STYLE;

public class TailFlagSystem extends Actuator {
    public TailFlagSystem(Connection connection) throws IOException {
        super(connection);
    }

    @Override
    protected String getActuatorName() {
        return Actuators.TAIL_FLAG;
    }

    @Override
    public void handle(byte[] message) {
        TailFlagAction tailFlagAction = TailFlagAction.fromByte(message[0]);
        System.out.println(Functions.formatColorReset(ACTUATOR_NAME_STYLE + Functions.center(
                "Tail Flag System", ACTUATOR_LENGTH), 1) + " Tail flag is positioned to be " + tailFlagAction);
    }
}
