package com.example.caffeine;

import com.example.caffeine.task.OrderRecursiveSubTask;
import com.example.caffeine.task.OrderTask;
import com.example.caffeine.task.PackageRecursiveSubTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringBootCaffeineApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("------------------: " + OrderTask.getOrderTask());
    }

}
