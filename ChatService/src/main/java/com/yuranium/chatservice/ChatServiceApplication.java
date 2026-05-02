package com.yuranium.chatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class ChatServiceApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ChatServiceApplication.class, args);
    }
}