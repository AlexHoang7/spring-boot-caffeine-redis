package com.example.caffeine.task;

import java.util.concurrent.RecursiveTask;

public class OrderRecursiveSubTask extends RecursiveTask<String> {

    private static final long serialVersionUID = -3936923893497874746L;

    @Override
    protected String compute() {
        System.out.println("order-----------");
//        try {
//            int result = 1 / 0;
//            return "orderSubTask";
//        } catch (Exception e) {
//            throw new GlobalException(ResponseEnum.RESPONSE_CODE_99.value(), ResponseEnum.RESPONSE_DESC_99.value());
//        }
        return "orderSubTask";
    }
}
