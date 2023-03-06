package sensory;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import utils.Commons;
import utils.Formats;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TemperatureSensor extends Sensory {
    private short temperature;

    public TemperatureSensor(Connection connection) throws IOException, TimeoutException {
        super(connection);
        temperature = Commons.STARTING_TEMPERATURE;
    }

    @Override
    public DeliverCallback onReceive() {
        return (consumerTag, delivery) -> {
            short changes = Functions.bytesToShort(delivery.getBody());
            if (changes == 0) {
                return;
            }

            temperature += changes;

            Formats.printSensor("Temperature Sensor", " New Temperature Detected: " + temperature +
                    "°C");

            publish(Functions.shortToBytes(temperature));
        };
    }

    @Override
    protected String getSensorName() {
        return Sensors.TEMPERATURE;
    }
}
