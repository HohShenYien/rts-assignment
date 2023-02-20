package actuators;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import utils.Actuators;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static utils.Formats.ACTUATOR_LENGTH;
import static utils.Formats.ACTUATOR_NAME_STYLE;

public class HeatingSystem extends Actuator {
    public HeatingSystem(Connection connection) throws IOException, TimeoutException {
        super(connection);
    }

    @Override
    protected String getActuatorName() {
        return Actuators.HEATING_SYSTEM;
    }

    @Override
    public DeliverCallback onReceive() {
        return (consumerTag, delivery) -> {
            short change = Functions.bytesToShort(delivery.getBody());

            System.out.println(Functions.formatColorReset(ACTUATOR_NAME_STYLE + Functions.center(
                    "Heating System", ACTUATOR_LENGTH), 1) + (change < 0 ? "Raising" : "Reducing") +
                    " Temperature by " + change + "°C");
            publishChange(Sensors.TEMPERATURE, Functions.shortToBytes(change));
        };
    }
}
