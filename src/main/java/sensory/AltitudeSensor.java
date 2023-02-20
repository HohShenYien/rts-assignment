package sensory;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import utils.Commons;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static utils.Formats.SENSOR_LENGTH;
import static utils.Formats.SENSOR_NAME_STYLE;

public class AltitudeSensor extends Sensory {
    public int altitude;

    public AltitudeSensor(Connection connection) throws IOException, TimeoutException {
        super(connection);
        altitude = Commons.STARTING_ALTITUDE;
    }

    @Override
    public DeliverCallback onReceive() throws IOException {
        return (consumerTag, delivery) -> {
            short changes = Functions.bytesToShort(delivery.getBody());
            if (changes == 0) {
                return;
            }

            altitude += changes;

            System.out.println(Functions.formatColorReset(SENSOR_NAME_STYLE + Functions.center(
                    "Altitude Sensor", SENSOR_LENGTH)) + " New Altitude Detected: " + altitude +
                    "m");

            publish(Functions.shortToBytes((short) altitude));
        };
    }

    @Override
    protected String getSensorName() {
        return Sensors.ALTITUDE;
    }
}
