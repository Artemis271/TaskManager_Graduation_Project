package com.artemis.taskservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Integration test — requires running PostgreSQL and Kafka")
@SpringBootTest
class TaskServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
