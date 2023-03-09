package actuators;

import com.rabbitmq.client.Connection;
import utils.Actuators;
import utils.Formats;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;

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

        Formats.printActuator("Thrust System", " " + (change > 0 ?
                "Raising" : "Lowering") + " the altitude by " + Math.abs(change) +
                "m");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        publishChange(Sensors.ALTITUDE, Functions.shortToBytes(change));
    }
}
