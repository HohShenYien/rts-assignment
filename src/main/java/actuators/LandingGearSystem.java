package actuators;

import com.rabbitmq.client.Connection;
import enums.PlaneMode;
import utils.Actuators;
import utils.Colors;
import utils.Formats;
import utils.Functions;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class LandingGearSystem extends Actuator {
    private final Connection connection;
    private final ExecutorService service;

    public LandingGearSystem(Connection connection, ExecutorService service) throws IOException {
        super(connection);
        this.connection = connection;
        this.service = service;
    }

    @Override
    protected String getActuatorName() {
        return Actuators.LANDING_GEAR;
    }

    @Override
    public void handle(byte[] message) {
        PlaneMode mode = PlaneMode.fromByte(message[0]);
        if (mode == PlaneMode.LANDING) {
            Formats.printActuator("Landing Gear System", " Landing now, plane gear " +
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
    }
}
