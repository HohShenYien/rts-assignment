package utils;

public class Formats {
    public static final int SENSOR_LENGTH = 30;
    public static final String SENSOR_NAME_STYLE = Colors.CYAN_BACKGROUND + Colors.BLACK;

    public static final int ACTUATOR_LENGTH = 26;
    public static final String ACTUATOR_NAME_STYLE = Colors.PURPLE_BACKGROUND + Colors.BLACK;

    public static final int CONTROL_SYSTEM_LENGTH = 22;
    public static final String CONTROL_SYSTEM_STYLE = Colors.GREEN_BACKGROUND + Colors.BLACK;

    public static void printControlSystem(String line) {
        System.out.println(Functions.formatColorReset(CONTROL_SYSTEM_STYLE + Functions.center(
                "Control System", CONTROL_SYSTEM_LENGTH), 2) + line);
    }

    public static void printSensor(String sensor, String line) {
        System.out.println(Functions.formatColorReset(SENSOR_NAME_STYLE + Functions.center(
                sensor, SENSOR_LENGTH)) + line);
    }

    public static void printActuator(String actuator, String line) {
        System.out.println(Functions.formatColorReset(ACTUATOR_NAME_STYLE + Functions.center(
                actuator, ACTUATOR_LENGTH), 1) + line);
    }
}
