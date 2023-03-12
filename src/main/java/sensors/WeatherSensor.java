package sensors;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import enums.Weather;
import utils.Formats;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class WeatherSensor extends Sensor {
    public WeatherSensor(Connection connection) throws IOException, TimeoutException {
        super(connection);
    }

    @Override
    public DeliverCallback onReceive() throws IOException {
        return (s, delivery) -> {
            Weather weather = Weather.fromByte(delivery.getBody()[0]);
            Formats.printSensor("Weather Sensor", " New Weather detected: " + weather);
            publish(delivery.getBody());
        };
    }

    @Override
    protected String getSensorName() {
        return Sensors.WEATHER;
    }
}
