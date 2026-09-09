package com.mj.choi.spring_boot.notification;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class SmsSender implements NotificationSender {
    @Override
    public void send(String message) {
        System.out.println("[sms] " + message);
    }
}
