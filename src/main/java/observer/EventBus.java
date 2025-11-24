package observer;
import java.util.*;

public enum EventBus implements EventIssuingObservable {
    INSTANCE;

    private final Map<EventType, Set<EventObserver>> observers = new EnumMap<>(EventType.class);

    public void attach(EventObserver observer) {
        attach(observer, EventType.All);
    }

    @Override
    public void attach(EventObserver observer, EventType type) {
        attach(observer, List.of(type));
    }

    @Override
    public void attach(EventObserver observer, List<EventType> types) {
        for (EventType type : types) {
            if (!observers.containsKey(type)) {
                observers.put(type, new HashSet<>());
            }
            observers.get(type).add(observer);
        }
    }


    public void detach(EventObserver observer) {
        for (EventType type : observers.keySet()) {
            observers.get(type).remove(observer);
        }
    }

    public void broadcast(EventType eventType, String message) {
        for (EventObserver observer : getObservers(eventType)) {
            observer.update(eventType, message);
        }
    }

    private Set<EventObserver> getObservers(EventType eventType) {
        Set<EventObserver> observersOfEvent = new HashSet<>();
        if (observers.containsKey(eventType)) {
            observersOfEvent.addAll(observers.get(eventType));
        }
        if (observers.containsKey(EventType.All)) {
            observersOfEvent.addAll(observers.get(EventType.All));
        }
        return observersOfEvent;
    }

    public void detachAll() {
        observers.clear();
    }
}
