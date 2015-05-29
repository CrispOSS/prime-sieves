package com.github.crisposs.sieves;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import abs.api.Configuration;
import abs.api.Context;
import abs.api.ContextThread;
import abs.api.LocalContext;
import abs.api.QueueInbox;

public class Main {

  private final ListCollector collector = new ListCollector();

  public Main(Long N, Duration duration) {
    Thread.setDefaultUncaughtExceptionHandler((thread, x) -> {
      System.err.println("Thread [" + thread + "]: " + x.getMessage());
    });
    ThreadFactory tf = r -> new ContextThread(r);
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(8, tf);
    Configuration configuration = Configuration.newConfiguration().withExecutorService(executor)
        .withInbox(new QueueInbox(executor)).build();
    Context context = new LocalContext(configuration);
    executor.schedule(shutdown(context), duration.getSeconds(), TimeUnit.SECONDS);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        context.stop();
      } catch (Exception e) {
        System.err.println("Context Shutdown: " + e.getMessage());
      }
      executor.shutdownNow();
    } , "executors"));
    Range range = new Range(1L, N, N);
    SieveMaster master = new SieveMaster(range, collector);
    context.newActor("master", master);
    Callable<Boolean> msg = () -> {
      master.sieve();
      return true;
    };
    Instant start = Instant.now();
    Future<Boolean> result = context.send(master, msg);
    try {
      assert result.get();
      Long lastPrime = master.lastPrime();
      Instant end = Instant.now();
      System.out.println("Took " + Duration.between(start, end).toMillis() + "(ms): " + lastPrime);
      context.stop();
    } catch (InterruptedException e) {
    } catch (ExecutionException e) {
    } catch (Exception e) {
    }
  }

  private Runnable shutdown(Context context) {
    Runnable r = () -> {
      try {
        context.stop();
      } catch (Exception e) {
      }
    };
    return r;
  }

  public Collection<Long> primes() {
    return collector.get().collect(Collectors.toList());
  }

  public static void main(String[] args) {
    Long N = 500_000L;
    if (args.length > 0) {
      N = Long.parseLong(args[0]);
    }
    try {
      new Main(N, Duration.ofHours(1));
    } catch (Exception e) {
    }
  }

}
