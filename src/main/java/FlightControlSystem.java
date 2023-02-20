import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import enums.*;
import utils.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static utils.Formats.CONTROL_SYSTEM_LENGTH;
import static utils.Formats.CONTROL_SYSTEM_STYLE;

public class FlightControlSystem implements Runnable {
    private final Channel channelIn;
    private final String queueNameIn;

    private final Channel channelOut;

    private CabinPressure pressure;
    private short altitude;
    private PlaneMode mode;

    public FlightControlSystem(Connection connection) throws IOException, TimeoutException {
        channelIn = connection.createChannel();
        channelIn.exchangeDeclare(Exchanges.SENSOR_OUTPUT, "direct");
        queueNameIn = channelIn.queueDeclare().getQueue();

        for (String sensor : List.of(Sensors.WEATHER, Sensors.CABIN_PRESSURE, Sensors.TEMPERATURE,
                Sensors.ALTITUDE, Sensors.LANDING_MODE)) {
            channelIn.queueBind(queueNameIn, Exchanges.SENSOR_OUTPUT, sensor);
        }

        channelOut = connection.createChannel();
        channelOut.exchangeDeclare(Exchanges.ACTUATOR_INPUT, "direct");
        altitude = Commons.STARTING_ALTITUDE;
    }

    @Override
    public void run() {
        try {
            channelIn.basicConsume(queueNameIn, true, (consumerTag, delivery) -> {
                byte[] body = delivery.getBody();
                switch (delivery.getEnvelope().getRoutingKey()) {
                    case Sensors.TEMPERATURE -> actOnTemperature(body);
                    case Sensors.LANDING_MODE -> actOnLandingMode(body);
                    case Sensors.CABIN_PRESSURE -> actOnPressure(body);
                    case Sensors.WEATHER -> actOnWeather(body);
                    case Sensors.ALTITUDE -> actOnAltitude(body);
                }
            }, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void publishAction(String actuator, byte[] change) {
        try {
            channelOut.basicPublish(Exchanges.ACTUATOR_INPUT, actuator, null, change);
        } catch (IOException e) {
            e.printStackTrace();
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
            System.out.println(Functions.formatColorReset(CONTROL_SYSTEM_STYLE + Functions.center(
                    "Control System", CONTROL_SYSTEM_LENGTH), 2) + " Temperature " + temperature +
                    "°C is higher than optimal temperature, " + (change < 0 ? "raising" :
                    "reducing") + " it by " + difference + "°C");
            publishAction(Actuators.HEATING_SYSTEM, Functions.shortToBytes(change));
        }
    }

    private void actOnLandingMode(byte[] message) {
        mode = PlaneMode.fromByte(message[0]);

        if (mode == PlaneMode.LANDING) {
            System.out.println(Functions.formatColorReset(CONTROL_SYSTEM_STYLE + Functions.center(
                    "Control System", CONTROL_SYSTEM_LENGTH), 2) + " Begins to land");
            System.out.println(Functions.formatColorReset(CONTROL_SYSTEM_STYLE + Functions.center(
                    "Control System", CONTROL_SYSTEM_LENGTH), 2) + " Plane is landing, lowering the " +
                    "altitude...");
            publishAction(Actuators.LANDING_GEAR, PlaneMode.toBytes(mode));
            publishAction(Actuators.THRUST_SYSTEM, Functions.shortToBytes((short) -altitude));
        }
    }

    private void actOnPressure(byte[] message) {
        pressure = CabinPressure.fromByte(message[0]);

        String action = pressure == CabinPressure.LOW ? "Low Cabin Pressure, Oxygen " +
                "Masks will be released to passengers" :
                "Cabin Pressure back to normal, Oxygen Masks will be kept back";

        OxygenMaskMode mode = pressure == CabinPressure.LOW ? OxygenMaskMode.DISTRIBUTE :
                OxygenMaskMode.KEEP;


        System.out.println(Functions.formatColorReset(CONTROL_SYSTEM_STYLE + Functions.center(
                "Control System", CONTROL_SYSTEM_LENGTH), 2) + " " + action);

        publishAction(Actuators.OXYGEN_MASK, OxygenMaskMode.toBytes(mode));

        if (pressure == CabinPressure.LOW) {
            if (altitude > Commons.SAFE_ALTITUDE_WHEN_LOW_PRESSURE) {
                short change = (short) (Commons.SAFE_ALTITUDE_WHEN_LOW_PRESSURE - altitude);
                System.out.println(Functions.formatColorReset(CONTROL_SYSTEM_STYLE + Functions.center(
                        "Control System", CONTROL_SYSTEM_LENGTH), 2) + " Cabin Low Pressure, " +
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

        System.out.println(Functions.formatColorReset(CONTROL_SYSTEM_STYLE + Functions.center(
                "Control System", CONTROL_SYSTEM_LENGTH), 2) + " " + action);

        publishAction(Actuators.TAIL_FLAG, TailFlagAction.toBytes(tailFlagAction));
    }

    private void actOnAltitude(byte[] message) {
        altitude = Functions.bytesToShort(message);
        short difference = (short) (altitude - Commons.OPTIMAL_ALTITUDE);

        if (difference == 0 || !needToChangeAltitude(difference)) {
            return;
        }

        System.out.println(Functions.formatColorReset(CONTROL_SYSTEM_STYLE + Functions.center(
                "Control System", CONTROL_SYSTEM_LENGTH), 2) + " Altitude " + altitude +
                "m is " + Functions.higherOrLower(difference) + " than optimal altitude, " + Functions.raisingOrReducing(difference) +
                " it by " + Math.abs(difference) + "m");

        publishAction(Actuators.THRUST_SYSTEM, Functions.shortToBytes((short) -difference));
    }

    private boolean needToChangeAltitude(int difference) {
        // Altitude lower is okay if cabin pressure is low or when landing
        if (difference < 0 && (pressure == CabinPressure.LOW || mode == PlaneMode.LANDING)) {
            return false;
        }
        return (Math.abs(difference) > Commons.OPTIMAL_ALTITUDE_RANGE);
    }
}
