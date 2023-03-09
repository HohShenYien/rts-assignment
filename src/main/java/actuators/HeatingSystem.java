package actuators;

import com.rabbitmq.client.Connection;
import utils.Actuators;
import utils.Formats;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;

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

        Formats.printActuator("Heating System", " " + (change < 0 ? "Reducing" : "Raising")
                + " Temperature by " + Math.abs(change) + "°C");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        publishChange(Sensors.TEMPERATURE, Functions.shortToBytes(change));
    }
}
