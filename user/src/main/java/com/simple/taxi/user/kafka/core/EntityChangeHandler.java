package com.simple.taxi.user.kafka.core;

public interface EntityChangeHandler {
    String getTopic();

    void onCreate(Object after);

    void onUpdate(Object after, Object before);

    void onDelete(Object before);
}
