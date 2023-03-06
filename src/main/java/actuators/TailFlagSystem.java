package actuators;

import com.rabbitmq.client.Connection;
import enums.TailFlagAction;
import utils.Actuators;
import utils.Formats;

import java.io.IOException;

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
        Formats.printActuator("Tail Flag System",
                " Tail flag is positioned to be " + tailFlagAction);
    }
}
