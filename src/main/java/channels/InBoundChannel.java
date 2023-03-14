package channels;

import benchmark.TimeManager;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;
import utils.Functions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InBoundChannel extends AbstractChannel {
    private final Set<String> routeKeys;

    public InBoundChannel(Connection connection, String exchangeName) throws IOException {
        super(connection, exchangeName);
        routeKeys = new HashSet<>();
    }

    public void consume(DeliverCallback handler) throws IOException {
        channel.basicConsume(queueName, true, (s, delivery) -> handlerWrapper(handler, s, delivery),
                consumerTag -> {
                });
    }

    public void bindQueue(String routeKey) throws IOException {
//        channel.queueBind(queueName, exchangeName, routeKey);
        routeKeys.add(routeKey);
    }

    private void handlerWrapper(DeliverCallback handler, String s, Delivery delivery) throws IOException {
        long nowInNano = System.nanoTime();
        byte[] body = delivery.getBody();

        short routeKeySize = Functions.bytesToShort(Arrays.copyOfRange(body, 8, 12));
        String routeKey = new String(Arrays.copyOfRange(body, 10, 10 + routeKeySize),
                StandardCharsets.UTF_8);

        if (!routeKeys.contains(routeKey)) {
            return;
        }

        // first 8 bytes are time in millis
        byte[] startTimeInNano = Arrays.copyOfRange(body, 0, 8);
        long duration = nowInNano - Functions.bytesToLong(startTimeInNano);

        TimeManager.addDuration(duration);

        Envelope messageEnvelope = delivery.getEnvelope();
        Envelope newEnvelope = new Envelope(messageEnvelope.getDeliveryTag(),
                messageEnvelope.isRedeliver(), messageEnvelope.getExchange(), routeKey);

        Delivery newDelivery = new Delivery(newEnvelope, delivery.getProperties(),
                Arrays.copyOfRange(body, 10 + routeKeySize, body.length));

        handler.handle(s, newDelivery);
    }

}
