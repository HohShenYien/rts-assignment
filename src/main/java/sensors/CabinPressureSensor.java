package sensors;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import enums.CabinPressure;
import utils.Formats;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CabinPressureSensor extends Sensor {
    public CabinPressureSensor(Connection connection) throws IOException, TimeoutException {
        super(connection);
    }

    @Override
    public DeliverCallback onReceive() {
        return (consumerTag, delivery) -> {
            CabinPressure pressure = CabinPressure.fromByte(delivery.getBody()[0]);

            Formats.printSensor("Cabin Pressure Sensor", " New Pressure Detected: " + pressure);

            publish(CabinPressure.toBytes(pressure));
        };
    }

    @Override
    protected String getSensorName() {
        return Sensors.CABIN_PRESSURE;
    }
}
