package jmh;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.openjdk.jmh.annotations.*;
import utils.Functions;
import utils.Sensors;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@State(Scope.Benchmark)
public class TestDirect {
    private Channel channel;
    private String exchangeName;
    private String routeKey;
    private byte[] message;

    @Setup(Level.Trial)
    public void setUp() throws IOException, TimeoutException {
        exchangeName = "test-exchange-direct";
        routeKey = Sensors.TEMPERATURE;
        message = Functions.shortToBytes((short) 20);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(exchangeName, "direct");
        channel.queueDeclare();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(1)
    @Timeout(time = 100)
    public void testPublish() throws IOException {
        for (int i = 0; i < 1000; i++) {
            channel.basicPublish(exchangeName, routeKey, null, message);
        }
    }
}
