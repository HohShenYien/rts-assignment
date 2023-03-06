package sensory;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import enums.PlaneMode;
import utils.Formats;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LandingSensor extends Sensory {
    public LandingSensor(Connection connection) throws IOException, TimeoutException {
        super(connection);
    }

    @Override
    public DeliverCallback onReceive() throws IOException {
        return (s, delivery) -> {
            PlaneMode mode = PlaneMode.fromByte(delivery.getBody()[0]);
            Formats.printSensor("Landing Mode Sensor", " New Mode Detected: " + mode);
            publish(delivery.getBody());
        };
    }

    @Override
    protected String getSensorName() {
        return Sensors.LANDING_MODE;
    }
}
