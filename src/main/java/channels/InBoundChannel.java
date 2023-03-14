package channels;

import benchmark.TimeManager;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
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
        channel.queueBind(queueName, exchangeName, routeKey);
    }

    private void handlerWrapper(DeliverCallback handler, String s, Delivery delivery) throws IOException {
        long nowInNano = System.nanoTime();
        byte[] body = delivery.getBody();
        // first 8 bytes are time in millis
        byte[] startTimeInNano = Arrays.copyOfRange(body, 0, 8);
        long duration = nowInNano - Functions.bytesToLong(startTimeInNano);

        TimeManager.addDuration(duration);

//        if (duration >= 3_000_000) {
//            Envelope envelope = delivery.getEnvelope();
//            TimeManager.addHighValues(envelope.getExchange() + " | " + envelope.getRoutingKey() + " : " + duration / 1_000_000 + "ms");
//        }

        Delivery newDelivery = new Delivery(delivery.getEnvelope(), delivery.getProperties(),
                Arrays.copyOfRange(body, 8, body.length));

        handler.handle(s, newDelivery);
    }

}
