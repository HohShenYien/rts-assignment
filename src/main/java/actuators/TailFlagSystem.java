package actuators;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import enums.TailFlagAction;
import utils.Actuators;
import utils.Functions;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static utils.Formats.ACTUATOR_LENGTH;
import static utils.Formats.ACTUATOR_NAME_STYLE;

public class TailFlagSystem extends Actuator {
    public TailFlagSystem(Connection connection) throws IOException, TimeoutException {
        super(connection);
    }

    @Override
    protected String getActuatorName() {
        return Actuators.TAIL_FLAG;
    }

    @Override
    public DeliverCallback onReceive() {
        return (s, delivery) -> {
            TailFlagAction tailFlagAction = TailFlagAction.fromByte(delivery.getBody()[0]);
            System.out.println(Functions.formatColorReset(ACTUATOR_NAME_STYLE + Functions.center(
                    "Tail Flag System", ACTUATOR_LENGTH), 1) + " Tail flag is positioned to be " + tailFlagAction);
        };
    }
}
