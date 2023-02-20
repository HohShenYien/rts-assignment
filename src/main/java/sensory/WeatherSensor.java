package sensory;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import enums.Weather;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static utils.Formats.SENSOR_LENGTH;
import static utils.Formats.SENSOR_NAME_STYLE;

public class WeatherSensor extends Sensory {
    public WeatherSensor(Connection connection) throws IOException, TimeoutException {
        super(connection);
    }

    @Override
    public DeliverCallback onReceive() throws IOException {
        return (s, delivery) -> {
            Weather weather = Weather.fromByte(delivery.getBody()[0]);
            System.out.println(Functions.formatColorReset(SENSOR_NAME_STYLE + Functions.center(
                    "Weather Sensor", SENSOR_LENGTH)) + " New Weather detected: " + weather);
            publish(delivery.getBody());
        };
    }

    @Override
    protected String getSensorName() {
        return Sensors.WEATHER;
    }
}
