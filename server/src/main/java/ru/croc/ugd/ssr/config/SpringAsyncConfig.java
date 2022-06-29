package ru.croc.ugd.ssr.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Конфигурация асинхронности для спринга.
 */
@Configuration
@EnableAsync
public class SpringAsyncConfig implements AsyncConfigurer {

    /**
     * Executor, который будет выполнять @Async методы.
     *
     * @return нужный executor
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setAllowCoreThreadTimeOut(true);
        executor.initialize();
        return executor;
    }

    /**
     * The {@link AsyncUncaughtExceptionHandler} instance to be used
     * when an exception is thrown during an asynchronous method execution
     * with {@code void} return type.
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

}
