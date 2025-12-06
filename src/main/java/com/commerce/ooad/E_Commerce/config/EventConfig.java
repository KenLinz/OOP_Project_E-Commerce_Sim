package com.commerce.ooad.E_Commerce.config;

import observer.EventBus;
import observer.EventType;
import observer.EmailNotificationObserver;

import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class EventConfig {
    @PostConstruct
    public void initializeObservers() {
        EventBus.INSTANCE.attach(new EmailNotificationObserver(), EventType.SuccessfulTransaction);
    }
}
