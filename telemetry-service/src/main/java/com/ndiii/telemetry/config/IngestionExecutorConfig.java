package com.ndiii.telemetry.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class IngestionExecutorConfig {

  @Bean
  public BlockingQueue<Runnable> ingestionQueue(@Value("${app.ingestion.maxQueue}") int maxQueue) {
    return new ArrayBlockingQueue<>(maxQueue);
  }

  @Bean
  public ExecutorService ingestionExecutor(
      @Value("${app.ingestion.workerThreads}") int threads,
      BlockingQueue<Runnable> ingestionQueue
  ) {
    ThreadPoolExecutor ex = new ThreadPoolExecutor(
        threads, threads,
        30, TimeUnit.SECONDS,
        ingestionQueue,
        r -> {
          Thread t = new Thread(r);
          t.setName("ingest-" + t.getId());
          t.setDaemon(true);
          return t;
        },
        new ThreadPoolExecutor.AbortPolicy() // triggers back-pressure
    );
    ex.allowCoreThreadTimeOut(true);
    return ex;
  }
}
