package com.example.caffeine.task;

import java.util.concurrent.RecursiveTask;

public class PackageRecursiveSubTask extends RecursiveTask<Integer> {

    private static final long serialVersionUID = 6112410413607479076L;

    @Override
    protected Integer compute() {
        System.out.println("package--------------");
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return 0;
    }
}
