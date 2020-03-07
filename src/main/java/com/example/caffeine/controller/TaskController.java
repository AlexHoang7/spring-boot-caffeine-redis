package com.example.caffeine.controller;

import com.example.caffeine.task.OrderRecursiveSubTask;
import com.example.caffeine.task.OrderTask;
import com.example.caffeine.task.PackageRecursiveSubTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    @GetMapping("/task")
    public String task() {
        String result = OrderTask.getOrderTask();
        System.out.println("result: " + result + "---------------------");
        return result;
    }
}
