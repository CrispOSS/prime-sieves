package com.github.crisposs.sieves;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import abs.api.Configuration;
import abs.api.Context;

public class Main {

  private final ListCollector collector = new ListCollector();

  public Main(Long N, Duration duration) {
    Context context = Configuration.newConfiguration().enableLogging().buildContext();
    Range range = new Range(1L, N, N);
    SieveMaster master = new SieveMaster(range, collector);
    context.newActor("master", master);
    Callable<Boolean> msg = () -> {
      master.sieve();
      return true;
    };
    Instant start = Instant.now();
    Future<Boolean> result = context.await(master, msg);
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
    Long N = 50000000L;
    if (args.length > 0) {
      N = Long.parseLong(args[0]);
    }
    try {
      new Main(N, Duration.ofHours(1));
    } catch (Exception e) {
    }
  }

}
