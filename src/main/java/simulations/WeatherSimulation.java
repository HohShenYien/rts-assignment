package simulations;

import com.rabbitmq.client.Connection;
import enums.Weather;
import utils.Colors;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

public class WeatherSimulation extends Simulation {
    private boolean inBadWeather = false;

    public WeatherSimulation(Connection connection, ScheduledExecutorService service) throws IOException,
            TimeoutException {
        super(connection, service);
    }

    @Override
    protected void simulate() {
        String condition = "";
        Weather weather;
        if (inBadWeather) {
            condition = "Weather is returned to normal";
            weather = Weather.NORMAL;
        } else {
            weather = Functions.getRandom(0, 1) > 0 ? Weather.STORM : Weather.TURBULENCE;
            condition = weather + " Weather ahead!";
        }
        inBadWeather = !inBadWeather;
        System.out.println();
        System.out.println(Functions.formatColorReset(Colors.RED + condition));
        publishChange(Sensors.WEATHER, Weather.toBytes(weather));
    }

    @Override
    protected boolean toContinue() {
        return true;
    }

    @Override
    protected int getIntervalInMillis() {
        return (inBadWeather ? Functions.getRandom(1, 2) : Functions.getRandom(2, 3)) * 20;
    }
}
