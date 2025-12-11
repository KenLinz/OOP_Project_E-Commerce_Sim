package observer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ObserverTest {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setup() {
        System.setOut(new PrintStream(outputStream));
        EventBus.INSTANCE.detachAll();
    }

    @AfterEach
    public void cleanup() {
        System.setOut(originalOut);
        EventBus.INSTANCE.detachAll();
    }

    @Test
    public void testAttachObserver() {
        EmailNotificationObserver observer = new EmailNotificationObserver();
        EventBus.INSTANCE.attach(observer);
        String message = "Email: test@email.com | Total: $100.00 | Items: 2 | Products: Laptop, Mouse";
        EventBus.INSTANCE.broadcast(EventType.SuccessfulTransaction, message);
        String output = outputStream.toString();
        assertTrue(output.contains("Sent an email to test@email.com"));
        assertTrue(output.contains("Order total was $100.00"));
    }

    @Test
    public void testAttachObserverWithMultipleEventTypes() {
        EmailNotificationObserver observer = new EmailNotificationObserver();
        EventBus.INSTANCE.attach(observer, List.of(EventType.SuccessfulTransaction, EventType.All));
        String message = "Email: jane@email.com | Total: $200.00 | Items: 3 | Products: Keyboard";
        EventBus.INSTANCE.broadcast(EventType.SuccessfulTransaction, message);
        String output = outputStream.toString();
        assertTrue(output.contains("Sent an email to jane@email.com"));
    }

    @Test
    public void testDetachObserver() {
        EmailNotificationObserver observer = new EmailNotificationObserver();
        EventBus.INSTANCE.attach(observer);
        EventBus.INSTANCE.detach(observer);
        String message = "Email: test@email.com | Total: $100.00 | Items: 2 | Products: Laptop";
        EventBus.INSTANCE.broadcast(EventType.SuccessfulTransaction, message);
        String output = outputStream.toString();
        assertTrue(output.isEmpty());
    }

    @Test
    public void testMultipleObservers() {
        EmailNotificationObserver observer1 = new EmailNotificationObserver();
        EmailNotificationObserver observer2 = new EmailNotificationObserver();
        EventBus.INSTANCE.attach(observer1);
        EventBus.INSTANCE.attach(observer2);
        String message = "Email: test@email.com | Total: $100.00 | Items: 2 | Products: Laptop";
        EventBus.INSTANCE.broadcast(EventType.SuccessfulTransaction, message);
        String output = outputStream.toString();
        int count = output.split("Sent an email to test@email.com").length - 1;
        assertEquals(2, count);
    }

    @Test
    public void testBroadcastToAll() {
        EmailNotificationObserver observer = new EmailNotificationObserver();
        EventBus.INSTANCE.attach(observer, EventType.All);
        String message = "Email: test@email.com | Total: $100.00 | Items: 2 | Products: Laptop";
        EventBus.INSTANCE.broadcast(EventType.SuccessfulTransaction, message);
        String output = outputStream.toString();
        assertTrue(output.contains("Sent an email to test@email.com"));
    }

    @Test
    public void testEmailNotificationParseError() {
        EmailNotificationObserver observer = new EmailNotificationObserver();
        EventBus.INSTANCE.attach(observer);
        EventBus.INSTANCE.broadcast(EventType.SuccessfulTransaction, "Invalid message format");
        String output = outputStream.toString();
        assertTrue(output.contains("Unable to parse message"));
    }

    @Test
    public void testDetachAll() {
        EmailNotificationObserver observer1 = new EmailNotificationObserver();
        EmailNotificationObserver observer2 = new EmailNotificationObserver();
        EventBus.INSTANCE.attach(observer1);
        EventBus.INSTANCE.attach(observer2);
        EventBus.INSTANCE.detachAll();
        String message = "Email: test@email.com | Total: $100.00 | Items: 2 | Products: Laptop";
        EventBus.INSTANCE.broadcast(EventType.SuccessfulTransaction, message);
        String output = outputStream.toString();
        assertTrue(output.isEmpty());
    }
}