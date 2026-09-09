package com.mj.choi.spring_boot.order;

import com.mj.choi.spring_boot.notification.NotificationSender;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final NotificationSender sender;

    public OrderService(NotificationSender sender) {
        this.sender = sender;
    }

    public void order(String item) {
        sender.send(item + "주문 완료");
    }
}
