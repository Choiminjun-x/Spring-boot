package com.mj.choi.spring_boot.notification;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
public class EmailSender implements NotificationSender {
    @Override
    public void send(String message) {
        System.out.println("[email] " + message);
    }
}
