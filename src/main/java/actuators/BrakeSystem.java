package actuators;

import com.rabbitmq.client.Connection;
import utils.Actuators;
import utils.Colors;
import utils.Formats;
import utils.Functions;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class BrakeSystem extends Actuator {
    private final Connection connection;
    private final ExecutorService service;

    public BrakeSystem(Connection connection, ExecutorService service) throws IOException {
        super(connection);
        this.connection = connection;
        this.service = service;
    }

    @Override
    protected String getActuatorName() {
        return Actuators.BRAKE_SYSTEM;
    }

    @Override
    public void handle(byte[] message) {
        short altitude = Functions.bytesToShort(message);
        if (altitude == 0) {
            Formats.printActuator("Brake System", " Landing now, brake is applied...");

            try {
                Thread.sleep(3000);
                System.out.println();
                System.out.println(Functions.formatColorReset(Colors.BLACK + Colors.GREEN_BACKGROUND + " Plane" +
                        " has landed successfully"));
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
