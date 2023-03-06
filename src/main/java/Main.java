import actuators.HeatingSystem;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import sensory.TemperatureSensor;
import simulations.TemperatureSimulation;
import utils.Colors;
import utils.Functions;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();

        ExecutorService service = Executors.newFixedThreadPool(20);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(12);

        System.out.println(Functions.formatColorReset(Colors.BLACK + Colors.YELLOW_BACKGROUND +
                " ======== Simulation begin ======== "));

        TemperatureSensor temperatureSensor = new TemperatureSensor(connection);
        service.submit(temperatureSensor);
        HeatingSystem heatingSystem = new HeatingSystem(connection);
        service.submit(heatingSystem);
        TemperatureSimulation temperatureSimulation = new TemperatureSimulation(connection, scheduler);
        service.submit(temperatureSimulation);
//
//        AltitudeSensor altitudeSensor = new AltitudeSensor(connection);
//        service.submit(altitudeSensor);
//        CabinPressureSensor cabinPressureSensor = new CabinPressureSensor(connection);
//        service.submit(cabinPressureSensor);
//        ThrustSystem thrustSystem = new ThrustSystem(connection);
//        service.submit(thrustSystem);
//        OxygenMaskSystem oxygenMaskSystem = new OxygenMaskSystem(connection);
//        service.submit(oxygenMaskSystem);
//        CabinPressureSimulation cabinPressureSimulation = new CabinPressureSimulation(connection,
//                scheduler);
//        service.submit(cabinPressureSimulation);
//
//        LandingSensor landingSensor = new LandingSensor(connection);
//        service.submit(landingSensor);
//        LandingGearSystem landingGearSystem = new LandingGearSystem(connection, service);
//        service.submit(landingGearSystem);
//        LandingSimulation landingSimulation = new LandingSimulation(connection, scheduler);
//        service.submit(landingSimulation);
//
//        WeatherSensor weatherSensor = new WeatherSensor(connection);
//        service.submit(weatherSensor);
//        TailFlagSystem tailFlagSystem = new TailFlagSystem(connection);
//        service.submit(tailFlagSystem);
//        WeatherSimulation weatherSimulation = new WeatherSimulation(connection, scheduler);
//        service.submit(weatherSimulation);

        EventManager eventManager = new EventManager(connection, scheduler);
        service.submit(eventManager);
        FlightControlSystem flightControlSystem = new FlightControlSystem(connection, eventManager);
        service.submit(flightControlSystem);
    }
}
