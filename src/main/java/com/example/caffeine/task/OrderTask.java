package com.example.caffeine.task;

import com.example.caffeine.util.SpringContextUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderTask extends RecursiveTask<String> {

    private static final long serialVersionUID = 5900053311036375733L;

    private OrderRecursiveSubTask orderRecursiveSubTask;

    private PackageRecursiveSubTask packageRecursiveSubTask;

    public OrderTask(OrderRecursiveSubTask orderRecursiveSubTask, PackageRecursiveSubTask packageRecursiveSubTask) {
        this.orderRecursiveSubTask = orderRecursiveSubTask;
        this.packageRecursiveSubTask = packageRecursiveSubTask;
    }

    @Override
    protected String compute() {
        invokeAll(orderRecursiveSubTask, packageRecursiveSubTask);
        int packageCode = packageRecursiveSubTask.join();
        try {
            int num = 1 / 0;
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return packageCode == 0 ? "package + " + orderRecursiveSubTask.join() : "error";
    }

    public static String getOrderTask() {
        ForkJoinPool forkJoinPool = SpringContextUtil.getBean(ForkJoinPool.class);
        String result = forkJoinPool.invoke(new OrderTask(new OrderRecursiveSubTask(), new PackageRecursiveSubTask()));
        System.out.println("-------------order task result: " + result + "-----------------");
        return result;
    }
}
