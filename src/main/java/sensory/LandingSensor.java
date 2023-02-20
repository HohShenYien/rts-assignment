package sensory;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import enums.PlaneMode;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static utils.Formats.SENSOR_LENGTH;
import static utils.Formats.SENSOR_NAME_STYLE;

public class LandingSensor extends Sensory {
    public LandingSensor(Connection connection) throws IOException, TimeoutException {
        super(connection);
    }

    @Override
    public DeliverCallback onReceive() throws IOException {
        return (s, delivery) -> {
            PlaneMode mode = PlaneMode.fromByte(delivery.getBody()[0]);
            System.out.println(Functions.formatColorReset(SENSOR_NAME_STYLE + Functions.center(
                    "Mode Sensor", SENSOR_LENGTH)) + " New Mode Detected: " + mode);
            publish(delivery.getBody());
        };
    }

    @Override
    protected String getSensorName() {
        return Sensors.LANDING_MODE;
    }
}
