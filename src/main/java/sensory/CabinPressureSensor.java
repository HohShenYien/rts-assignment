package sensory;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import enums.CabinPressure;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static utils.Formats.SENSOR_LENGTH;
import static utils.Formats.SENSOR_NAME_STYLE;

public class CabinPressureSensor extends Sensory {
    public CabinPressureSensor(Connection connection) throws IOException, TimeoutException {
        super(connection);
    }

    @Override
    public DeliverCallback onReceive() throws IOException {
        return (consumerTag, delivery) -> {
            CabinPressure pressure = CabinPressure.fromByte(delivery.getBody()[0]);

            System.out.println(Functions.formatColorReset(SENSOR_NAME_STYLE + Functions.center(
                    "Cabin Pressure Sensor", SENSOR_LENGTH)) + " New Pressure Detected: " + pressure);

            publish(CabinPressure.toBytes(pressure));
        };
    }

    @Override
    protected String getSensorName() {
        return Sensors.CABIN_PRESSURE;
    }
}
