package com.artemis.chatservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Integration test — requires running MongoDB and Kafka")
@SpringBootTest
class ChatServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
