package com.chiril.library.notification;

public final class ConsoleNotificationChannel implements NotificationChannel {
    @Override
    public void send(String memberId, String message) {
        System.out.println("[NOTIFY][" + memberId + "] " + message);
    }
}
