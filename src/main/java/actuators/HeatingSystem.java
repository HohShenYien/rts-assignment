package actuators;

import com.rabbitmq.client.Connection;
import utils.Actuators;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;

import static utils.Formats.ACTUATOR_LENGTH;
import static utils.Formats.ACTUATOR_NAME_STYLE;

public class HeatingSystem extends Actuator {
    public HeatingSystem(Connection connection) throws IOException {
        super(connection);
    }

    @Override
    protected String getActuatorName() {
        return Actuators.HEATING_SYSTEM;
    }

    @Override
    public void handle(byte[] message) {

        short change = Functions.bytesToShort(message);

        System.out.println(Functions.formatColorReset(ACTUATOR_NAME_STYLE + Functions.center(
                "Heating System", ACTUATOR_LENGTH), 1) + (change < 0 ? "Raising" : "Reducing") +
                " Temperature by " + change + "°C");
        publishChange(Sensors.TEMPERATURE, Functions.shortToBytes(change));
    }
}
