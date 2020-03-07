package com.example.caffeine.configuration;

import com.example.caffeine.exception.GlobalException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ForkJoinPoolFactoryBean;

@Configuration
public class projectConfiguration {

    @Bean
    public ForkJoinPoolFactoryBean forkJoinPoolFactoryBean() {
        ForkJoinPoolFactoryBean forkJoinPoolFactoryBean = new ForkJoinPoolFactoryBean();
        forkJoinPoolFactoryBean.setUncaughtExceptionHandler(((Thread t, Throwable e) -> {
            if (e instanceof GlobalException) {
                System.out.println("------------------uncaughtException---------------");
            }
            System.out.println("fork join error");
        }));
        return forkJoinPoolFactoryBean;
    }
}
