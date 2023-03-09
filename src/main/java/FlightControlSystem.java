import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import enums.*;
import utils.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FlightControlSystem implements Runnable {
    private final Channel channelIn;
    private final String queueNameIn;

    private CabinPressure pressure;
    private short altitude;
    private PlaneMode mode;

    private final AtomicInteger logId;
    private final EventManager eventManager;

    public FlightControlSystem(Connection connection, EventManager eventManager) throws IOException {
        channelIn = connection.createChannel();
        channelIn.exchangeDeclare(Exchanges.SENSOR_OUTPUT, "direct");
        queueNameIn = channelIn.queueDeclare().getQueue();

        for (String sensor : List.of(Sensors.WEATHER, Sensors.CABIN_PRESSURE, Sensors.TEMPERATURE,
                Sensors.ALTITUDE, Sensors.LANDING_MODE)) {
            channelIn.queueBind(queueNameIn, Exchanges.SENSOR_OUTPUT, sensor);
        }

        altitude = Commons.STARTING_ALTITUDE;

        logId = new AtomicInteger(0);
        this.eventManager = eventManager;
    }

    @Override
    public void run() {
        try {
            channelIn.basicConsume(queueNameIn, true, (consumerTag, delivery) -> {
                byte[] body = delivery.getBody();
                actOnChange(body, delivery.getEnvelope().getRoutingKey());

            }, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void publishAction(String actuator, byte[] change) {
        int id = logId.getAndIncrement();
        eventManager.addLog(id, change, actuator);
        eventManager.publishAction(id, actuator, change);
    }

    private void actOnChange(byte[] message, String key) {
        switch (key) {
            case Sensors.TEMPERATURE -> actOnTemperature(message);
            case Sensors.LANDING_MODE -> actOnLandingMode(message);
            case Sensors.CABIN_PRESSURE -> actOnPressure(message);
            case Sensors.WEATHER -> actOnWeather(message);
            case Sensors.ALTITUDE -> actOnAltitude(message);
        }
    }

    private void actOnTemperature(byte[] message) {
        short temperature = Functions.bytesToShort(message);
        short difference = (short) (Math.abs(temperature - Commons.OPTIMAL_TEMPERATURE) / 4 * 3);
        short change = 0;
        if (temperature < Commons.OPTIMAL_TEMPERATURE - Commons.OPTIMAL_TEMPERATURE_RANGE) {
            change = difference;
        } else if (temperature > Commons.OPTIMAL_TEMPERATURE + Commons.OPTIMAL_TEMPERATURE_RANGE) {
            change = (short) -difference;
        }
        if (change != 0) {
            Formats.printControlSystem(" Temperature " + temperature +
                    "°C is higher than optimal temperature, " + (change < 0 ? "reducing" :
                    "raising") + " it by " + difference + "°C");
            publishAction(Actuators.HEATING_SYSTEM, Functions.shortToBytes(change));
        }
    }

    private void actOnLandingMode(byte[] message) {
        mode = PlaneMode.fromByte(message[0]);

        if (mode == PlaneMode.LANDING) {
            Formats.printControlSystem(" Begins to land");
            Formats.printControlSystem(" Plane is landing, lowering the altitude...");
            publishAction(Actuators.THRUST_SYSTEM, Functions.shortToBytes((short) -1000));
        }
    }

    private void actOnPressure(byte[] message) {
        pressure = CabinPressure.fromByte(message[0]);

        String action = pressure == CabinPressure.LOW ? "Low Cabin Pressure, Oxygen " +
                "Masks will be released to passengers" :
                "Cabin Pressure back to normal, Oxygen Masks will be kept back";

        OxygenMaskMode mode = pressure == CabinPressure.LOW ? OxygenMaskMode.DISTRIBUTE :
                OxygenMaskMode.KEEP;


        Formats.printControlSystem(" " + action);

        publishAction(Actuators.OXYGEN_MASK, OxygenMaskMode.toBytes(mode));

        if (pressure == CabinPressure.LOW) {
            if (altitude > Commons.SAFE_ALTITUDE_WHEN_LOW_PRESSURE) {
                short change = (short) (Commons.SAFE_ALTITUDE_WHEN_LOW_PRESSURE - altitude);
                Formats.printControlSystem(" Cabin Low Pressure, " +
                        "reducing airplane altitude to " + Commons.SAFE_ALTITUDE_WHEN_LOW_PRESSURE + "m");
                publishAction(Actuators.THRUST_SYSTEM, Functions.shortToBytes(change));
            }
        } else {
            // back to normal pressure, check altitude again
            actOnAltitude(Functions.shortToBytes(altitude));
        }
    }

    private void actOnWeather(byte[] message) {
        Weather weather = Weather.fromByte(message[0]);
        TailFlagAction tailFlagAction = weather == Weather.NORMAL ? TailFlagAction.NORMAL :
                TailFlagAction.AWAY;
        String action;

        if (weather == Weather.NORMAL) {
            action = "Airplane is diverted back to normal path";
        } else {
            action = "Bad weather ahead, airplane is diverted from normal path";
        }

        Formats.printControlSystem(" " + action);

        publishAction(Actuators.TAIL_FLAG, TailFlagAction.toBytes(tailFlagAction));
    }

    private void actOnAltitude(byte[] message) {
        altitude = Functions.bytesToShort(message);
        short difference = (short) (altitude - Commons.OPTIMAL_ALTITUDE);

        if (mode != PlaneMode.LANDING && (difference == 0 || !needToChangeAltitude(difference))) {
            return;
        }

        if (mode == PlaneMode.LANDING) {
            if (altitude == 0) {
                Formats.printControlSystem(" Plane reaches the ground, applying brake...");
                publishAction(Actuators.BRAKE_SYSTEM, Functions.shortToBytes(altitude));
                return;
            }
            difference = (short) Math.min(1000, altitude);
            if (altitude <= 1000) {
                // opens landing gear
                Formats.printControlSystem(" Plane is below 1000m, opening landing gear");
                publishAction(Actuators.LANDING_GEAR, PlaneMode.toBytes(mode));
            }
            Formats.printControlSystem(" Plane is landing, lowering altitude by " + difference +
                    "m...");
        } else {
            Formats.printControlSystem(" Altitude " + altitude + "m is " +
                    Functions.higherOrLower(difference) + " than optimal altitude, " + Functions.raisingOrReducing(difference) +
                    " it by " + Math.abs(difference) + "m");
        }

        publishAction(Actuators.THRUST_SYSTEM, Functions.shortToBytes((short) -difference));
    }

    private boolean needToChangeAltitude(int difference) {
        // Altitude lower is okay if cabin pressure is low or when landing
        if (difference < 0 && (pressure == CabinPressure.LOW)) {
            return false;
        }
        return (Math.abs(difference) > Commons.OPTIMAL_ALTITUDE_RANGE);
    }

}
