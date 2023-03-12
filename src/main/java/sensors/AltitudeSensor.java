package sensors;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import utils.Commons;
import utils.Formats;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AltitudeSensor extends Sensor {
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

            Formats.printSensor("Altitude Sensor", " New Altitude Detected: " + altitude +
                    "m");

            publish(Functions.shortToBytes((short) altitude));
        };
    }

    @Override
    protected String getSensorName() {
        return Sensors.ALTITUDE;
    }
}
