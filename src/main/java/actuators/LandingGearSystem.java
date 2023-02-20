package actuators;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import enums.PlaneMode;
import utils.Actuators;
import utils.Colors;
import utils.Functions;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import static utils.Formats.ACTUATOR_LENGTH;
import static utils.Formats.ACTUATOR_NAME_STYLE;

public class LandingGearSystem extends Actuator {
    private final Connection connection;
    private final ExecutorService service;

    public LandingGearSystem(Connection connection, ExecutorService service) throws IOException,
            TimeoutException {
        super(connection);
        this.connection = connection;
        this.service = service;
    }

    @Override
    protected String getActuatorName() {
        return Actuators.LANDING_GEAR;
    }

    @Override
    public DeliverCallback onReceive() {
        return (s, delivery) -> {
            PlaneMode mode = PlaneMode.fromByte(delivery.getBody()[0]);
            if (mode == PlaneMode.LANDING) {
                System.out.println(Functions.formatColorReset(ACTUATOR_NAME_STYLE + Functions.center(
                        "Landing Gear System", ACTUATOR_LENGTH), 1) + " Landing now, plane gear " +
                        "is opened...");

                try {
                    Thread.sleep(3000);
                    System.out.println();
                    System.out.println(Functions.formatColorReset(Colors.BLACK + Colors.GREEN_BACKGROUND + " Plane" +
                            " has landed "));
                    System.out.println(Functions.formatColorReset(Colors.BLACK + Colors.YELLOW_BACKGROUND +
                            " ======== Simulation ended ======== "));
                    connection.abort();
                    service.shutdownNow();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
