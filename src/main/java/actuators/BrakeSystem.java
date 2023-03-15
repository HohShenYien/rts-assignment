package actuators;

import EventManager.EventManager;
import com.rabbitmq.client.Connection;
import utils.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class BrakeSystem extends Actuator {
    private final Connection connection;
    private final ExecutorService service;
    private final EventManager manager;

    public BrakeSystem(Connection connection, ExecutorService service, EventManager manager) throws IOException {
        super(connection);
        this.connection = connection;
        this.service = service;
        this.manager = manager;
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

                printFailure();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void printFailure() {
        int received = 0;
        for (Log log : manager.logStore.values()) {
            if (log.received()) {
                received++;
            }
        }
        System.out.println("=".repeat(20) + " Reliability Test " + "=".repeat(20));
        System.out.println("Total Messages Received:\t" + received);
        System.out.println("Total Messages: \t\t\t" + manager.logStore.size());
        System.out.println("Total Error Simulated:\t\t" + Actuator.errorSimulationCount);
        System.out.println("Error Rate: \t\t\t\t" + (manager.logStore.size() - received) / (double) manager.logStore.size() * 100 + "%");
    }
}
