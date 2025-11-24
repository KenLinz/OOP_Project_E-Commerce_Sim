package observer;

public interface EventObserver {
    void update(EventType event, String message);
}
