package observer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailNotificationObserver implements EventObserver {

    private static final Pattern MESSAGE_PATTERN = Pattern.compile(
            "Email:\\s*([^|]+)\\s*\\|\\s*Total:\\s*([^|]+)\\s*\\|\\s*Items:\\s*([^|]+)\\s*\\|\\s*Products:\\s*(.+)"
    );

    @Override
    public void update(EventType event, String message) {
        if (event == EventType.SuccessfulTransaction) {
            Matcher matcher = MESSAGE_PATTERN.matcher(message);

            if (matcher.find()) {
                String email = matcher.group(1).trim();
                String total = matcher.group(2).trim();
                String itemCount = matcher.group(3).trim();
                String productNames = matcher.group(4).trim();

                System.out.println("Sent an email to " + email +
                        ". Order total was " + total +
                        " with " + itemCount + " products purchased: " + productNames);
            } else {
                System.out.println("Email notification: Unable to parse message - " + message);
            }
        }
    }
}