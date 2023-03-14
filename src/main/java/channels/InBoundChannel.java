package channels;

import benchmark.TimeManager;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;
import utils.Functions;

import java.io.IOException;
import java.util.Arrays;

public class InBoundChannel extends AbstractChannel {
    public InBoundChannel(Connection connection, String exchangeName) throws IOException {
        super(connection, exchangeName);
    }

    public void consume(DeliverCallback handler) throws IOException {
        channel.basicConsume(queueName, true, (s, delivery) -> handlerWrapper(handler, s, delivery),
                consumerTag -> {
                });
    }

    public void bindQueue(String routeKey) throws IOException {
        channel.queueBind(queueName, "Topic", exchangeName + "." + routeKey);
    }

    private void handlerWrapper(DeliverCallback handler, String s, Delivery delivery) throws IOException {
        long nowInNano = System.nanoTime();
        byte[] body = delivery.getBody();

        // first 8 bytes are time in millis
        byte[] startTimeInNano = Arrays.copyOfRange(body, 0, 8);
        long duration = nowInNano - Functions.bytesToLong(startTimeInNano);

        TimeManager.addDuration(duration);

        Envelope messageEnvelope = delivery.getEnvelope();
        String[] routeKeys = messageEnvelope.getRoutingKey().split("\\.");
        String routeKey = routeKeys.length > 1 ? routeKeys[1] : "";
        Envelope newEnvelope = new Envelope(messageEnvelope.getDeliveryTag(),
                messageEnvelope.isRedeliver(), messageEnvelope.getExchange(), routeKey);

        Delivery newDelivery = new Delivery(newEnvelope, delivery.getProperties(),
                Arrays.copyOfRange(body, 8, body.length));

        handler.handle(s, newDelivery);
    }

}
