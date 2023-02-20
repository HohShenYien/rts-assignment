package actuators;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import enums.OxygenMaskMode;
import utils.Actuators;
import utils.Functions;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static utils.Formats.ACTUATOR_LENGTH;
import static utils.Formats.ACTUATOR_NAME_STYLE;

public class OxygenMaskSystem extends Actuator {
    public OxygenMaskSystem(Connection connection) throws IOException, TimeoutException {
        super(connection);
    }

    @Override
    protected String getActuatorName() {
        return Actuators.OXYGEN_MASK;
    }

    @Override
    public DeliverCallback onReceive() {
        return (consumerTag, delivery) -> {
            OxygenMaskMode action = OxygenMaskMode.fromByte(delivery.getBody()[0]);
            System.out.println(Functions.formatColorReset(ACTUATOR_NAME_STYLE + Functions.center(
                    "Oxygen Mask System", ACTUATOR_LENGTH), 1) + " " + action);
        };
    }
}
