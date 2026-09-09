package com.mj.choi.spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        String[] names = context.getBeanDefinitionNames();
        System.out.println("=== Bean 개수: " + names.length + " ===");

        for (String name : names) {
            if (name.startsWith("emailSender")
                    || name.startsWith("smsSender")
                    || name.startsWith("orderService")
                    || name.startsWith("orderController")) {
                System.out.println(name + " → " + context.getBean(name).getClass().getName());
            }
        }
    }
}