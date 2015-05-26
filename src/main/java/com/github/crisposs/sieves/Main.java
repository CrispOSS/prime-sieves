package com.github.crisposs.sieves;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import abs.api.Actor;
import abs.api.Configuration;
import abs.api.Context;
import abs.api.LocalContext;
import abs.api.QueueInbox;

public class Main {

  private final ListCollector collector = new ListCollector();

  public Main(Long N) throws InterruptedException, ExecutionException {
    Thread.setDefaultUncaughtExceptionHandler((thread, x) -> {
      System.err.println("Thread [" + thread + "]: " + x.getMessage());
    });
    ExecutorService executor = Executors.newCachedThreadPool();
    Configuration configuration = Configuration.newConfiguration().withExecutorService(executor)
        .withInbox(new QueueInbox(executor)).build();
    Context context = new LocalContext(configuration);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        context.stop();
      } catch (Exception e) {
        System.err.println("Context Shutdown: " + e.getMessage());
      }
      executor.shutdownNow();
    } , "executors"));
    Range range = new Range(1L, N);
    SieveMaster master = new SieveMaster(range, collector);
    Actor actor = context.newActor("master", master);
    Callable<Boolean> msg = () -> {
      master.sieve();
      return true;
    };
    Instant start = Instant.now();
    Future<Boolean> result = context.send(master, msg);
    assert result.get();
    Long lastPrime = master.lastPrime();
    Instant end = Instant.now();
    System.out.println("Took " + Duration.between(start, end) + " : " + lastPrime);
    executor.shutdownNow();
  }

  public Collection<Long> primes() {
    return collector.get().collect(Collectors.toList());
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    new Main(10000L);
  }

}
