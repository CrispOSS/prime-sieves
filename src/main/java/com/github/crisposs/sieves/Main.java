package com.github.crisposs.sieves;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import abs.api.Configuration;
import abs.api.Context;
import abs.api.LocalContext;

public class Main {

  private final ListCollector collector = new ListCollector();

  public Main(Long N, Duration duration) {
    Thread.setDefaultUncaughtExceptionHandler((thread, x) -> {
      System.err.println("Thread [" + thread + "]: " + x.getMessage());
    });
    Configuration configuration = Configuration.newConfiguration().build();
    Context context = new LocalContext(configuration);
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

  public Collection<Long> primes() {
    return collector.get();
  }

  public static void main(String[] args) {
    Long N = 5000_000L;
    if (args.length > 0) {
      N = Long.parseLong(args[0]);
    }
    try {
      new Main(N, Duration.ofHours(1));
    } catch (Exception e) {
    }
  }

}
